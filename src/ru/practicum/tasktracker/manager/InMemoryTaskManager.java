package ru.practicum.tasktracker.manager;

import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.ValidationException;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    final static Comparator<Task> COMPARATOR = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected Set<Task> prioritizedTasks = new TreeSet<>(COMPARATOR);

    private int id;

    public InMemoryTaskManager() {
    }

    @Override
    public Task createTask(Task task) {
        validate(task);
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        validate(subtask);
        subtask.setId(getNextId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        setEpicDateTime(subtask.getEpicId());
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        validate(task);
        tasks.replace(taskId, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null || !epics.containsKey(epicId)) {
            return null;
        }
        Epic earlyEpic = epics.get(epicId);
        List<Subtask> earlyEpicSubtaskList = earlyEpic.getSubtaskList();
        if (!earlyEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : earlyEpicSubtaskList) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.replace(epicId, epic);
        List<Subtask> newEpicSubtaskList = epic.getSubtaskList();
        if (!newEpicSubtaskList.isEmpty()) {
            for (Subtask subtask : newEpicSubtaskList) {
                subtasks.put(subtask.getId(), subtask);
            }
        }
        updateEpicStatus(epic);
        setEpicDateTime(epic.getId());
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return null;
        }
        int epicId = subtask.getEpicId();
        Subtask earlySubtask = subtasks.get(subtaskId);
        subtasks.replace(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        List<Subtask> subtaskList = epic.getSubtaskList();
        subtaskList.remove(earlySubtask);
        validate(subtask);
        subtaskList.add(subtask);
        epic.setSubtaskList(subtaskList);
        updateEpicStatus(epic);
        setEpicDateTime(epicId);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public void deleteTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskList().clear();
            for (Subtask subtask : subtasks.values()) {
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getEpicId());
            }
        }
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        for (Epic epic : epics.values()) {
            epic.getSubtaskList().clear();
            updateEpicStatus(epic);
            setEpicDateTime(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            prioritizedTasks.remove(tasks.get(taskId));
            tasks.remove(taskId);
            historyManager.remove(taskId);
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            List<Subtask> epicSubtasks = epic.getSubtaskList();
            for (Subtask subtask : epicSubtasks) {
                prioritizedTasks.remove(subtask);
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            epics.remove(epic.getId());
            historyManager.remove(epic.getId());
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);

        if (subtask != null) {
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            List<Subtask> subtaskList = epic.getSubtaskList();
            subtaskList.remove(subtask);
            epic.setSubtaskList(subtaskList);
            updateEpicStatus(epic);
            setEpicDateTime(epicId);
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtask);
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    public void updateEpicStatus(Epic epic) {
        List<Subtask> list = epic.getSubtaskList();

        int allDone = 0;
        int allNew = 0;

        for (Subtask subtask : list) {
            if (subtask.getStatus() == Status.DONE) {
                allDone++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allNew++;
            }
        }
        if (allDone == list.size()) {
            epic.setStatus(Status.DONE);
        } else if (allNew == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected int getNextId() {
        return ++id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void setEpicDateTime(int epicId) {

        List<Subtask> subtasks = epics.get(epicId).getSubtaskList();
        LocalDateTime startTime = subtasks.getFirst().getStartTime();
        LocalDateTime endTime = subtasks.getFirst().getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) startTime = subtask.getStartTime();
            if (subtask.getEndTime().isAfter(endTime)) endTime = subtask.getEndTime();
        }

        epics.get(epicId).setStartTime(startTime);
        epics.get(epicId).setEndTime(endTime);
        Duration duration = Duration.between(startTime, endTime);
        epics.get(epicId).setDuration(duration);
    }

    private boolean checkTasksOverlapTime(Task task, Task existTask) {
        return !(task.getStartTime().isAfter(existTask.getEndTime()) ||
                task.getEndTime().isBefore(existTask.getStartTime()));
    }

    public List<Task> getPrioritizedTasks() {
        tasks.values().stream()
                .filter(Objects::nonNull)
                .filter(task -> task.getStartTime() != null)
                .forEach(prioritizedTasks::add);
        subtasks.values().stream()
                .filter(Objects::nonNull)
                .filter(subTask -> subTask.getStartTime() != null)
                .forEach(prioritizedTasks::add);
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void validate(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        prioritizedTasks.stream()
                .filter(existTask -> !existTask.getId().equals(task.getId()))
                .filter(existTask -> checkTasksOverlapTime(task, existTask))
                .findFirst()
                .ifPresent(existTask -> {
                    throw new ValidationException("Время выполнения задачи пересекается со временем уже существующей " +
                            "задачи. Выберите другую дату.");
                });
    }
}