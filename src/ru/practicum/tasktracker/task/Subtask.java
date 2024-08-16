package ru.practicum.tasktracker.task;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Status status, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status, LocalDateTime startTime,
                   Duration duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", startTime='" + getStartTimeString() + '\'' +
                ", duration='" + getDuration().toMinutes() + '\'' +
                ", epicID='" + getEpicId() + '\'' +
                '}';
    }
}