package ru.practicum.tasktracker.task;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtaskList = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration,
                LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);

    }

    public void clearSubtasks() {
        subtaskList.clear();

    }

    public void setSubtaskList(List<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String getEndTimeString() {
        if (endTime == null) {
            return "null";
        }
        return LocalDateAdapter.formatter(endTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        if (subtaskList.isEmpty()) {
            return "Epic{" +
                    "id=" + getId() + '\'' +
                    ", name= " + getName() + '\'' +
                    ", description = " + getDescription() + '\'' +
                    ", status = " + getStatus() + '\'' +
                    '}';
        } else {
            return "Epic{" +
                    "id=" + getId() + '\'' +
                    ", name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", status='" + getStatus() + '\'' +
                    ", дата начала='" + getStartTimeString() + '\'' +
                    ", продолжительность='" + getDuration().toMinutes();
        }
    }
}