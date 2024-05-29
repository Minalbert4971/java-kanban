package ru.practicum.task_tracker.task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(String name, String description, Status status, ArrayList<Subtask> subtaskList) {
        super(name, description, status);
        this.subtaskList = subtaskList;
    }

    public Epic(Integer id, String name, String description, ArrayList<Subtask> subtaskList) {
        super(id, name, description);
        this.subtaskList = subtaskList;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);

    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }
}