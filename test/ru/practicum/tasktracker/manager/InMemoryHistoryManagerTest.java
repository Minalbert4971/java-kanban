package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;
    final LocalDateTime DATE = LocalDateTime.of(2024, 8, 16, 1, 0);

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task(1, "Задача", "description1", Status.NEW, DATE, Duration.ofMinutes(1));
        epic = new Epic(2, "Эпик", "description3", Status.NEW);
        subtask = new Subtask(3, "Подзадача", "description3", Status.NEW,
                DATE.plusDays(1), Duration.ofMinutes(1), 2);
        subtask2 = new Subtask(4, "Подзадача", "description3", Status.NEW,
                DATE.plusDays(2), Duration.ofMinutes(1), 2);
    }

    @Test
    void add() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertEquals(1, history.size(), "История пустая");
        assertEquals(1, task.getId(), "История сохранена неверно");
    }

    @Test
    void getHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertTrue(history.isEmpty(), "История не пустая");

        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не сохранена");

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не пустая");

        historyManager.add(task);
        historyManager.add(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История сохранена неверно");
        assertEquals(1, task.getId(), "История сохранена неверно");
    }

    @Test
    void remove() {
        // Удаление из начала, середниы и конца
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "Список истории отсутствует");
        assertEquals(3, history.size(), "История сохранена неверно");

        historyManager.remove(1);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История сохранена неверно");
        assertEquals(2, history.getFirst().getId(), "История сохранена неверно");
        assertEquals(3, history.get(1).getId(), "История сохранена неверно");

        historyManager.add(subtask2);
        historyManager.remove(3);
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История сохранена неверно");
        assertEquals(2, history.get(0).getId(), "История сохранена неверно");
        assertEquals(4, history.get(1).getId(), "История сохранена неверно");

        historyManager.remove(4);
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История сохранена неверно");
        assertEquals(2, history.getFirst().getId(), "История сохранена неверно");
    }
}