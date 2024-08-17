package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(String fileName) {
        this.file = new File(fileName);
    }

    public void loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> lines = reader.lines().toList();
            lines.stream()
                    .skip(1)
                    .map(l -> l.split(","))
                    .forEach(this::handleLine);
        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
    }

    private void handleLine(String[] line) {
        Task task = CSVFormatter.fromString(line);
        switch (Objects.requireNonNull(task).getType()) {
            case TASK:
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic) task);
                break;
            case SUBTASK:
                subtasks.put(task.getId(), (Subtask) task);
                int epicId = task.getEpicId();
                List<Subtask> subtasks = epics.get(epicId).getSubtaskList();
                subtasks.add((Subtask) task);
                updateEpicStatus(epics.get(epicId));
                prioritizedTasks.add(task);
                break;
        }
    }

    protected void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bw.write(CSVFormatter.getHeader());
            bw.newLine();
            tasks.values().forEach(task -> {
                try {
                    bw.write(CSVFormatter.toString(task));
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            epics.values().forEach(epic -> {
                try {
                    bw.write(CSVFormatter.toString(epic));
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            subtasks.values().forEach(subtask -> {
                try {
                    bw.write(CSVFormatter.toString(subtask));
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw ManagerSaveException.saveException(e);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void setEpicDateTime(int epicId) {
        super.setEpicDateTime(epicId);
        save();
    }
}