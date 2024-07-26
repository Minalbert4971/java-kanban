package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.enums.Type;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String toString(Task task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getName() + "," +
                task.getDescription() + "," +
                task.getStatus();
    }

    public static String toString(Subtask subtask) {
        return subtask.getId() + "," +
                subtask.getType() + "," +
                subtask.getName() + "," +
                subtask.getDescription() + "," +
                subtask.getStatus() + "," +
                subtask.getEpicId();
    }

    public static String toString(Epic epic) {
        return epic.getId() + "," +
                epic.getType() + "," +
                epic.getName() + "," +
                epic.getDescription() + "," +
                epic.getStatus();
    }

    public static Task fromString(String[] line) {
        int id = Integer.parseInt(line[0]);
        Type type = Type.valueOf(line[1]);
        String name = line[2];
        String description = line[3];
        Status status = Status.valueOf(line[4]);

        return switch (type) {
            case TASK -> new Task(id, name, description, status);
            case EPIC -> new Epic(id, name, description, status);
            case SUBTASK -> {
                int epicId = Integer.parseInt(line[5]);
                yield new Subtask(id, name, description, status, epicId);
            }
        };
    }

    public static String getHeader() {
        return "id,type,name,description,status,epicId";
    }
}
