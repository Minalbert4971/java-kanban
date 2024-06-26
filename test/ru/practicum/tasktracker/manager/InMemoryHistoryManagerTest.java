package ru.practicum.tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private static TaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefault();
    }

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 20; i++) {
            inMemoryTaskManager.createTask(new Task("Имя", "Описание"));
        }

        List<Task> tasks = inMemoryTaskManager.getTasks();
        for (Task task : tasks) {
            inMemoryTaskManager.getTask(task.getId());
        }

        List<Task> list = inMemoryTaskManager.getHistory();
        assertEquals(10, list.size(), "Неверное количество элементов в истории ");
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task task1 = new Task("Имя", "Описание");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.getTask(task1.getId());
        inMemoryTaskManager.updateTask(new Task(task1.getId(), "Новое имя",
                "Новое описание", Status.IN_PROGRESS));
        List<Task> tasks = inMemoryTaskManager.getHistory();
        Task oldTask = tasks.getFirst();
        assertEquals(task1.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");
        assertEquals(task1.getDescription(), oldTask.getDescription(),
                "В истории не сохранилась старая версия задачи");
    }

    @Test
    public void getHistoryShouldReturnOldEpicAfterUpdate() {
        Epic epic1 = new Epic("Имя", "Описание", Status.NEW);
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.getEpic(epic1.getId());
        inMemoryTaskManager.updateEpic(new Epic("Новое имя", "Новое описание"
        ));
        List<Task> epics = inMemoryTaskManager.getHistory();
        Epic oldEpic = (Epic) epics.getFirst();
        assertEquals(epic1.getName(), oldEpic.getName(),
                "В истории не сохранилась старая версия эпика");
        assertEquals(epic1.getDescription(), oldEpic.getDescription(),
                "В истории не сохранилась старая версия эпика");
    }

    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        Epic epic1 = new Epic("Имя эпика", "Описание эпика", Status.NEW);
        inMemoryTaskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Имя сабтаска1", "Описание сабтаска1",
                Status.NEW, epic1.getId());
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.getSubtask(subtask1.getId());
        inMemoryTaskManager.updateSubtask(new Subtask(subtask1.getId(), "Новое имя сабтаска",
                "Новое описание сабтаска", Status.IN_PROGRESS, epic1.getId()));
        List<Task> subtasks = inMemoryTaskManager.getHistory();
        Subtask oldSubtask = (Subtask) subtasks.getFirst();
        assertEquals(subtask1.getName(), oldSubtask.getName(),
                "В истории не сохранилась старая версия сабтаска");
        assertEquals(subtask1.getDescription(), oldSubtask.getDescription(),
                "В истории не сохранилась старая версия сабтаска");
    }
}