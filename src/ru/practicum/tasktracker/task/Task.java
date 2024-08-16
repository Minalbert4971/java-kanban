package ru.practicum.tasktracker.task;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Integer id;
    private final String name;
    private final String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getEpicId() {
        return null;
    }

    public Type getType() {
        return Type.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        if (startTime == null) {
            return "null";
        }
        return LocalDateAdapter.formatter(startTime);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration.toMinutes());
        } else {
            return null;
        }
    }

    public String getEndTimeString() {
        if (startTime != null) {
            return LocalDateAdapter.formatter(getEndTime());
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && status == task.status &&
                Objects.equals(startTime, task.startTime) && (duration == task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", дата начала='" + getStartTimeString() + '\'' +
                ", продолжительность='" + getDuration().toMinutes() + '}' + '\'';
    }
}