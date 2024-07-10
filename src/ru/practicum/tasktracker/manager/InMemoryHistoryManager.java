package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static class MyLinkedList {
        private final Map<Integer, Node> nodes = new HashMap<>();
        private Node head;
        private Node tail;

        private void linkLast(Task task) {
            Node node = new Node();
            node.setTask(task);

            if (nodes.containsKey(task.getId())) {
                removeNode(nodes.get(task.getId()));
            }

            if (head == null) {
                head = node;
                tail = node;
                node.setNext(null);
                node.setPrev(null);
            } else {
                node.setNext(null);
                node.setPrev(tail);
                tail.setNext(node);
                tail = node;
            }

            nodes.put(task.getId(), node);
        }

        private List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            Node node = head;
            while (node != null) {
                taskList.add(node.getTask());
                node = node.getNext();
            }
            return taskList;
        }

        private void removeNode(Node node) {
            if (node != null) {
                nodes.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }

                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        private Node getNode(int id) {
            return nodes.get(id);
        }
    }

    private final MyLinkedList myLinkedList = new MyLinkedList();

    @Override
    public void add(Task task) {
        myLinkedList.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return myLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        myLinkedList.removeNode(myLinkedList.getNode(id));
    }
}