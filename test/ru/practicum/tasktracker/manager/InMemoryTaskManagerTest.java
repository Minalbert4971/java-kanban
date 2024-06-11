package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private static TaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        final Task task = inMemoryTaskManager.createTask(new Task("Test addNewTask", "Test addNewTask description"));
        final Task savedTask = inMemoryTaskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = inMemoryTaskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicAndSubtasks() {
        final Epic epic1 = inMemoryTaskManager.createEpic(new Epic("Test addNewEpic",
                "Test addNewEpic description", Status.NEW));
        final Subtask subtask1 = inMemoryTaskManager.createSubtask(new Subtask("Test addNewSubtask1",
                "Subtask1 description", Status.NEW, epic1.getId()));
        final Subtask subtask2 = inMemoryTaskManager.createSubtask(new Subtask("Test addNewSubtask2",
                "Subtask2 description", Status.NEW, epic1.getId()));
        final Subtask subtask3 = inMemoryTaskManager.createSubtask(new Subtask("Test addNewSubtask3",
                "Subtask3 description", Status.NEW, epic1.getId()));
        final Epic savedEpic = inMemoryTaskManager.getEpic(epic1.getId());
        final Subtask savedSubtask1 = inMemoryTaskManager.getSubtask(subtask1.getId());
        final Subtask savedSubtask2 = inMemoryTaskManager.getSubtask(subtask2.getId());
        final Subtask savedSubtask3 = inMemoryTaskManager.getSubtask(subtask3.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotNull(savedSubtask2, "Подзадача не найдена.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");
        assertEquals(subtask1, savedSubtask1, "Подзадачи не совпадают.");
        assertEquals(subtask3, savedSubtask3, "Подзадачи не совпадают.");

        final List<Epic> epics = inMemoryTaskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epics.getFirst(), "Эпики не совпадают.");

        final List<Subtask> subtasks = inMemoryTaskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    public void deleteTasksShouldReturnEmptyList() {
        inMemoryTaskManager.createTask(new Task("Имя1", "Описание1"));
        inMemoryTaskManager.createTask(new Task("Имя2", "Описание2"));
        inMemoryTaskManager.deleteTasks();
        List<Task> tasks = inMemoryTaskManager.getTasks();
        assertTrue(tasks.isEmpty(), "После удаления задач список должен быть пуст.");
    }

    @Test
    public void deleteEpicsShouldReturnEmptyList() {
        inMemoryTaskManager.createEpic(new Epic("Имя", "Описание", Status.NEW));
        inMemoryTaskManager.deleteEpics();
        List<Epic> epics = inMemoryTaskManager.getEpics();
        assertTrue(epics.isEmpty(), "После удаления эпиков список должен быть пуст.");
    }

    @Test
    public void deleteSubtasksShouldReturnEmptyList() {
        Epic epic1 = new Epic("Имя эпика1", "Описание эпика1", Status.NEW);
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.createSubtask(new Subtask("Имя сабтаски1", "Описание сабтаски1",
                Status.NEW, epic1.getId()));
        inMemoryTaskManager.createSubtask(new Subtask("Имя сабтаски2", "Описание сабтаски2",
                Status.NEW, epic1.getId()));
        inMemoryTaskManager.createSubtask(new Subtask("Имя сабтаски3", "Описание сабтаски3",
                Status.NEW, epic1.getId()));
        inMemoryTaskManager.deleteSubtasks();
        List<Subtask> subtasks = inMemoryTaskManager.getSubtasks();
        assertTrue(subtasks.isEmpty(), "После удаления подзадач список должен быть пуст.");
    }
}