package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager taskManager = new FileBackedTaskManager();
    private File file;
    protected Task task1;
    protected Epic epic2;
    protected Subtask subtask3;
    protected Subtask subtask4;

    @BeforeEach
    void setUp() {
        file = new File("resources/kanban.csv");
        taskManager = new FileBackedTaskManager();
        task1 = new Task("Задача1", "Описание1", Status.NEW);
        taskManager.createTask(task1);
        epic2 = new Epic("Эпик2", "Описание2", Status.NEW);
        taskManager.createEpic(epic2);
        subtask3 = new Subtask("Подзадача3", "Описание3", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask3);
        subtask4 = new Subtask("Подзадача4", "Описание4", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask4);

        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        taskManager.getSubtask(4);
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager fileManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, fileManager.tasks.size(), "Несоответствие количества задач после чтения");
        assertEquals(taskManager.getTasks(), fileManager.getTasks(),
                "Несоответствие списка задач после чтения");
        assertEquals(1, fileManager.epics.size(), "Несоответствие количества эпиков после чтения");
        assertEquals(taskManager.getEpics(), fileManager.getEpics(),
                "Несоответствие списка эпиков после чтения");
        assertEquals(2, fileManager.subtasks.size(),
                "Несоответствие количества подзадач после чтения");
        assertEquals(taskManager.getSubtasks(), fileManager.getSubtasks(),
                "Несоответствие списка подзадач после чтения");
        assertEquals(4, taskManager.id,
                "Несоответствие id последней добавленной задачи после чтения");
    }

    @AfterEach
    void tearDown() {
        if ((file.exists())) {
            assertTrue(file.delete());
        }
    }
}
