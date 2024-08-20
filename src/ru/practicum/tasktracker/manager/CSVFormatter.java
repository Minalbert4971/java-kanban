package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Type;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String toString(Task task) {
        String result;
        if (task.getStartTime() != null && task.getDuration() != null && task.getEndTimeString() != null) {
            result = task.getId() + "," + task.getType() + "," + task.getName() + "," +
                    task.getDescription() + "," + task.getStatus() + "," + task.getStartTimeString() + "," +
                    task.getEndTimeString() + "," + task.getDuration().toMinutes();
        } else {
            result = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getDescription() + "," +
                    task.getStatus() + "," + null + "," + null + "," + null;
        }
        return result;
    }

    public static String toString(Subtask subtask) {
        String result;
        if (subtask.getStartTime() != null && subtask.getDuration() != null && subtask.getEndTimeString() != null) {
            result = subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," +
                    subtask.getDescription() + "," + subtask.getStatus() + "," + subtask.getStartTimeString() + "," +
                    subtask.getEndTimeString() + "," + subtask.getDuration().toMinutes() + "," + subtask.getEpicId();
        } else {
            result = subtask.getId() + "," + subtask.getType() + "," + subtask.getName() + "," +
                    subtask.getDescription() + "," + subtask.getStatus() + "," + null + "," + null + "," + null + "," +
                    subtask.getEpicId();
        }
        return result;
    }

    public static String toString(Epic epic) {
        String result;
        if (epic.getStartTime() != null && epic.getDuration() != null && epic.getEndTimeString() != null) {
            result = epic.getId() + "," + epic.getType() + "," + epic.getName() + "," + epic.getDescription() + "," +
                    epic.getStatus() + "," + epic.getStartTimeString() + "," + epic.getEndTimeString() + "," +
                    epic.getDuration().toMinutes();
        } else {
            result = epic.getId() + "," + epic.getType() + "," + epic.getName() + "," + epic.getDescription() + "," +
                    epic.getStatus() + "," + null + "," + null + "," + null;
        }
        return result;
    }

    public static Task fromString(String[] line) {
        int id = Integer.parseInt(line[0]);
        Type type = Type.valueOf(line[1]);
        String name = line[2];
        String description = line[3];
        Status status = Status.valueOf(line[4]);
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (!line[5].equals("null")) {
            startTime = LocalDateTime.parse(line[5], DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"));
            endTime = LocalDateTime.parse(line[6], DateTimeFormatter.ofPattern("dd.MM.yy HH:mm"));
        } else {
            startTime = null;
            endTime = null;
        }
        Duration duration = Duration.ofMinutes(Long.parseLong(line[7]));
        return switch (type) {
            case TASK -> new Task(id, name, description, status, startTime, duration);
            case EPIC -> new Epic(id, name, description, status, startTime, duration, endTime);
            case SUBTASK -> {
                int epicId = Integer.parseInt(line[8]);
                yield new Subtask(id, name, description, status, startTime, duration, epicId);
            }
        };
    }

    public static String getHeader() {
        return "id,type,name,description,status, startTime, duration, epicId";
    }
}