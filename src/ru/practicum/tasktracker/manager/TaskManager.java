package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.util.List;

public interface TaskManager {
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subtaskId);

    List<Task> getHistory();

    void remove(int id);

    List<Task> getPrioritizedTasks();

    void validate(Task task);

    void setEpicDateTime(int epicId);

    void clearPrioritizedTasks();
}