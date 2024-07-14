package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.task.Task;

public class Node {
    private Task task;
    private Node prev;
    private Node next;

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public Task getTask() {
        return task;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
