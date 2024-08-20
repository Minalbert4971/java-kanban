package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @BeforeEach
    void setUp() {
        file = new File("resources/kanban.csv");
        super.taskManager = new FileBackedTaskManager("resources/test.csv");
        initTasks();
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager fileManager = super.taskManager;
        fileManager.loadFromFile();
        assertEquals(1, fileManager.tasks.size(), "Несоответствие количества задач после чтения");
        assertEquals(taskManager.getTasks(), fileManager.getTasks(),
                "Несоответствие списка задач после чтения");
        assertEquals(1, fileManager.epics.size(), "Несоответствие количества эпиков после чтения");
        assertEquals(taskManager.getEpics(), fileManager.getEpics(),
                "Несоответствие списка эпиков после чтения");
        assertEquals(2, fileManager.subtasks.size(),
                "Несоответствие количества подзадач после чтения");
        assertEquals(taskManager.getTasks(), fileManager.getTasks(),
                "Несоответствие списка подзадач после чтения");
        assertEquals(taskManager.getPrioritizedTasks(), fileManager.getPrioritizedTasks(),
                "Несоответствие отсортированного списка после чтения");
        assertEquals(4, taskManager.getId(),
                "Несоответствие id последней добавленной задачи после чтения");
    }

    @AfterEach
    void tearDown() {
        if ((file.exists())) {
            assertTrue(file.delete());
        }
    }
}