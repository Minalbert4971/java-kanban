package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        super.taskManager = new InMemoryTaskManager();
        initTasks();
    }
}