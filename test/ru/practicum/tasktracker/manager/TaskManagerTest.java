package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.ValidationException;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected final LocalDateTime DATE = LocalDateTime.of(2024, 8, 14, 0, 0);
    protected final int EPIC_ID = 2;
    protected Task task1;
    protected Epic epic2;
    protected Subtask subtask3;
    protected Subtask subtask4;

    protected void initTasks() {
        task1 = new Task(1, "Задача", "description1", Status.NEW, DATE, Duration.ofMinutes(1));
        taskManager.createTask(task1);
        epic2 = new Epic(2, "Эпик", "description2", Status.NEW);
        taskManager.createEpic(epic2);
        subtask3 = new Subtask(3, "Подзадача", "description3", Status.NEW, DATE.plusDays(1),
                Duration.ofMinutes(1), EPIC_ID);
        taskManager.createSubtask(subtask3);
        subtask4 = new Subtask(4, "Подзадача", "description4", Status.NEW, DATE.plusDays(2),
                Duration.ofMinutes(1), EPIC_ID);
        taskManager.createSubtask(subtask4);
        taskManager.updateEpic(epic2);
    }

    @Test
    void addTask() {
        Task expectedTask = taskManager.getTask(1);
        assertNotNull(expectedTask, "Задача не найдена");
        assertNotNull(taskManager.getTasks(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач");
        assertEquals(1, expectedTask.getId(), "Идентификаторы задач не совпадают");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(taskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(taskPriority, expectedTask, "В список приоритизации добавлена неверная задача");
    }

    @Test
    void addEpic() {
        Epic expectedEpic = taskManager.getEpic(2);
        assertNotNull(expectedEpic, "Задача не найдена.");
        assertNotNull(taskManager.getEpics(), "Задачи не возвращаются.");
        assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач.");
        assertNotNull(expectedEpic.getSubtaskList(), "Список подзадач не создан.");
        assertEquals(Status.NEW, expectedEpic.getStatus(), "Статус не NEW");
        assertEquals(2, expectedEpic.getId(), "Идентификаторы задач не совпадают");
    }

    @Test
    void addSubtask() {
        Epic expectedEpicOfSubtask = taskManager.getEpic(EPIC_ID);
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не null");
        Subtask expectedSubtask = taskManager.getSubtask(3);
        assertNotNull(expectedSubtask, "Задача не найдена.");
        assertNotNull(taskManager.getSubtasks(), "Задачи на возвращаются.");
        assertEquals(2, taskManager.getSubtasks().size(), "Неверное количество задач.");
        assertNotNull(expectedEpicOfSubtask, "Эпик подзадачи не найден");
        assertNotNull(taskManager.getSubtasks(), "Список подзадач не обновился");
        assertEquals(DATE.plusDays(1), expectedEpicOfSubtask.getStartTime(), "Время эпика не обновилось");
        assertEquals(Status.NEW, expectedEpicOfSubtask.getStatus(), "Статус не NEW");
        assertEquals(3, expectedSubtask.getId(), "Идентификаторы задач не совпадают");
        assertEquals(expectedEpicOfSubtask, epic2, "Эпик подзадачи неверный");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(subtaskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(subtaskPriority, expectedSubtask, "В список приоритизации добавлена неверная задача");
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не изменилось");
    }

    @Test
    void updateEpicStatus() {
        Epic expectedEpicOfSubtask = taskManager.getEpic(EPIC_ID);
        // статус epic должен быть IN_PROGRESS, если статусы subtask NEW и DONE
        Subtask updateSubtask4 = new Subtask(4, "Подзадача", "description4", Status.DONE,
                DATE.plusDays(2), Duration.ofMinutes(1), EPIC_ID);
        taskManager.updateSubtask(updateSubtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        // статус epic должен быть DONE, если статусы subtask DONE
        Subtask updateSubtask3 = new Subtask(3, "Подзадача", "description3", Status.DONE,
                DATE.plusDays(1), Duration.ofMinutes(1), EPIC_ID);
        Subtask update2Subtask4 = new Subtask(4, "Подзадача", "description4", Status.DONE,
                DATE.plusDays(2), Duration.ofMinutes(1), EPIC_ID);
        taskManager.updateSubtask(updateSubtask3);
        taskManager.updateSubtask(update2Subtask4);
        assertEquals(Status.DONE, expectedEpicOfSubtask.getStatus(), "Статус не DONE");
        // статус epic должен быть IN_PROGRESS, если статусы subtask IN_PROGRESS
        Subtask update2Subtask3 = new Subtask(3, "Подзадача", "description3",
                Status.IN_PROGRESS, DATE.plusDays(1), Duration.ofMinutes(1), EPIC_ID);
        Subtask update3Subtask4 = new Subtask(4, "Подзадача", "description4",
                Status.IN_PROGRESS, DATE.plusDays(2), Duration.ofMinutes(1), EPIC_ID);
        taskManager.updateSubtask(update2Subtask3);
        taskManager.updateSubtask(update3Subtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
    }

    @Test
    void getPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.get(0).getId(), "Задача 1 не приоритизирована");
        assertEquals(3, prioritizedTasks.get(1).getId(), "Задача 3 не приоритизирована");
        assertEquals(4, prioritizedTasks.get(2).getId(), "Задача 4 не приоритизирована");
    }

    @Test
    void deleteTask() {
        assertNotNull(taskManager.getTasks(), "Список задач не заполнен");
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач.");
        taskManager.deleteTask(1);
        assertNull(taskManager.getTask(1), "Задача не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNull(taskPriority, "Задача не удалена из списка приоритизации");
    }

    @Test
    void deleteSubtask() {
        assertNotNull(taskManager.getSubtasks(), "Список подзадач не заполнен");
        assertEquals(2, taskManager.getSubtasks().size(), "Неверное количество задач.");
        taskManager.deleteSubtask(3);
        assertNull(taskManager.getSubtask(3), "Подзадача не удалена");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNull(subtaskPriority, "Задача не удалена из списка приоритизации");
    }

    @Test
    void deleteEpic() {
        assertNotNull(taskManager.getEpics(), "Список эпиков не заполнен");
        taskManager.deleteEpic(2);
        assertNull(taskManager.getEpic(2), "Эпик не удален");
    }

    @Test
    void validate() {
        Task task1 = new Task("Задача1", "description1", Status.NEW, DATE, Duration.ofMinutes(1));
        Task task2 = new Task("Задача2", "description2", Status.NEW, DATE, Duration.ofMinutes(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> {
                    taskManager.createTask(task1);
                    taskManager.createTask(task2);
                });
        assertEquals("Время выполнения задачи пересекается со временем уже существующей " +
                "задачи. Выберите другую дату.", exception.getMessage());
    }
}