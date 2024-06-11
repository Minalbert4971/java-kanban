package ru.practicum.tasktracker;

import ru.practicum.tasktracker.manager.*;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        testTasks();
        testEpicsAndSubtasks();
        printViewHistory();
    }

    private static void testTasks() {
        InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

        System.out.println("Тест 1: Пустой список Tasks");
        List<Task> tasks = inMemoryTaskManager.getTasks();
        System.out.println("Список Tasks должен быть пустым: " + tasks.isEmpty());
        System.out.println();

        System.out.println("Тест 2: Создание Task");
        Task task1 = new Task("Имя", "Описание", Status.NEW);
        Task task1Created = inMemoryTaskManager.createTask(task1);
        System.out.println("Созданная Task должна содержать id: " + (task1Created.getId() != null));
        System.out.println("Список Tasks должен содержать эту Task: " + inMemoryTaskManager.getTasks());
        System.out.println();

        System.out.println("Тест 3: Обновление Task");
        Task task2 = new Task(task1Created.getId(), "Имя новое", "Описание новое", Status.IN_PROGRESS);
        Task task2Updated = inMemoryTaskManager.updateTask(task2);
        System.out.println("Обновленная Task должна иметь обновленные поля: " + task2Updated);
        System.out.println();

        System.out.println("Тест 4: Удаление Task");
        boolean deleteRes = inMemoryTaskManager.deleteTask(task2Updated.getId());
        System.out.println("Удаление должно пройти успешно: " + deleteRes);
        System.out.println("Список Tasks должен быть пустым: " + inMemoryTaskManager.getTasks());
        System.out.println();
    }

    private static void testEpicsAndSubtasks() {
        InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

        System.out.println("Тест 5: Пустые списки Epics и Subtasks");
        List<Epic> epics = inMemoryTaskManager.getEpics();
        List<Subtask> subtasks = inMemoryTaskManager.getSubtasks();
        System.out.println("Список Epics и Subtasks должны быть пустые: " + epics.isEmpty() + " " + subtasks.isEmpty());
        System.out.println();

        System.out.println("Тест 6: Создание Epic");
        Epic epic1 = new Epic("Имя эпика", "Описание эпика", Status.NEW);
        Task epic1Created = inMemoryTaskManager.createEpic(epic1);
        System.out.println("Созданный Epic должен содержать id: " + (epic1Created.getId() != null));
        System.out.println("Список Epics должен содержать этот Epic: " + inMemoryTaskManager.getEpics());
        System.out.println();

        System.out.println("Тест 7: Создание Subtask1 в Epic");
        Subtask subtask1 = new Subtask("Имя сабтаска1", "Описание сабтаска1",
                Status.NEW, epic1Created.getId());
        Task subtask1Created = inMemoryTaskManager.createSubtask(subtask1);
        System.out.println("Созданный Subtask1 должен содержать id: " + (subtask1Created.getId() != null));
        System.out.println("Список Subtasks должен содержать этот Subtask1: " + inMemoryTaskManager.getSubtasks());
        System.out.println();

        System.out.println("Тест 8: Создание Subtask2 в Epic");
        Subtask subtask2 = new Subtask("Имя сабтаска2", "Описание сабтаска2",
                Status.NEW, epic1Created.getId());
        Task subtask2Created = inMemoryTaskManager.createSubtask(subtask2);
        System.out.println("Созданный Subtask2 должен содержать id: " + (subtask2Created.getId() != null));
        System.out.println("Список Subtasks должен содержать этот Subtask2 и остальные Subtask: "
                + inMemoryTaskManager.getSubtasks());
        System.out.println();

        System.out.println("Тест 9: Обновление Subtask1, списка Subtasks и статуса Epic");
        Subtask subtask3 = new Subtask(subtask1Created.getId(),"Имя Subtask новое",
                "Описание Subtask новое",
                Status.IN_PROGRESS, epic1Created.getId());
        Subtask subtask3Updated = inMemoryTaskManager.updateSubtask(subtask3);
        System.out.println("Обновленная Subtask3 должна иметь обновленные поля: " + subtask3Updated +
                "Список Subtasks должен обновиться: " + inMemoryTaskManager.getSubtasks() +
                "И статус Epic1 должен обновиться: " + epic1Created.getStatus());
        System.out.println();

        System.out.println("Тест 10: Удаление Subtask3, смена статусов Subtask2 и Epic");
        boolean deleteRes = inMemoryTaskManager.deleteSubtask(subtask3Updated.getId());
        System.out.println("Удаление должно пройти успешно: " + deleteRes);
        subtask2Created.setStatus(Status.DONE);
        System.out.println("Обновляем статус Subtask2: " + subtask2Created.getStatus());
        System.out.println("Список Subtasks должен содержать только один Subtask2: " + inMemoryTaskManager.getSubtasks());
        Epic epic1Updated = inMemoryTaskManager.updateEpic(epic1);
        System.out.println("Статус Epic также должен обновиться: " + epic1Updated.getStatus());
        System.out.println();

        System.out.println("Тест 11: Удаление Epic");
        boolean deleteRes1 = inMemoryTaskManager.deleteEpic(epic1Updated.getId());
        System.out.println("Удаление должно пройти успешно: " + deleteRes1);
        System.out.println("Список Epics должен быть пустым: " + inMemoryTaskManager.getEpics());
        System.out.println("Список Subtasks должен быть пустым: " + inMemoryTaskManager.getSubtasks());
        System.out.println();
    }

    private static void printViewHistory() {
        InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();

        System.out.println("Тест 12: Вывод списка истории просмотра 10 задач");
        Task task = new Task("Имя таски1", "Описание таски1", Status.NEW);
        Task taskCreated = inMemoryTaskManager.createTask(task);

        Epic epic = new Epic("Имя эпика1", "Описание эпика1", Status.NEW);
        Task epicCreated = inMemoryTaskManager.createEpic(epic);

        Subtask subtask = new Subtask("Имя сабтаска1", "Описание сабтаска1",
                Status.NEW, epicCreated.getId());
        Task subtaskCreated = inMemoryTaskManager.createSubtask(subtask);

        // Имитируем 11 просмотров задач, в выводе истории должно отобразиться только 10
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getSubtask(3);
        System.out.println("История просмотров:");
        System.out.println();

        for (Task tasks : inMemoryTaskManager.getHistory()) {
            System.out.println(tasks);
        }
    }
}