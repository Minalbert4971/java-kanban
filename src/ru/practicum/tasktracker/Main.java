package ru.practicum.tasktracker;

import ru.practicum.tasktracker.manager.*;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {

//        testTasks();
//
//        testEpicsAndSubtasks();

        printViewHistory();
    }

    // Доп задание спринта 6:
    private static void printViewHistory() {
        TaskManager taskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());

        System.out.println("Тест истории");
        System.out.println("Создание задач");
        taskManager.createTask(new Task("Описание-1", "Task-1", Status.NEW)); // id 1
        taskManager.createTask(new Task("Описание-2", "Task-2", Status.NEW)); // id 2
        taskManager.createEpic(new Epic("Описание-1", "Epic-1", Status.NEW)); // id 3
        taskManager.createEpic(new Epic("Описание-1", "Epic-2", Status.NEW)); // id 4
        taskManager.createSubtask(new Subtask("Описание-1", "Subtask-1", Status.NEW, 3)); // id 5
        taskManager.createSubtask(new Subtask("Описание-2", "Subtask-2", Status.NEW, 3)); // id 6
        taskManager.createSubtask(new Subtask("Описание-3", "Subtask-3", Status.NEW, 3)); // id 7

        System.out.println("Имитация просмотра задач");
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getEpic(3);
        taskManager.getEpic(3);
        taskManager.getTask(1);
        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);

        System.out.println("Просмотр истории");
        List<Task> history = taskManager.getHistory();
        System.out.println(history);

        System.out.println("Удаление из истории");
        taskManager.remove(1);
        taskManager.deleteEpic(3);

        List<Task> historyAfterRemove = taskManager.getHistory();
        System.out.println(historyAfterRemove);
    }

//    private static void testTasks() {
//        TaskManager inMemoryTaskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
//
//        System.out.println("Тест 1: Пустой список Tasks");
//        List<Task> tasks = inMemoryTaskManager.getTasks();
//        System.out.println("Список Tasks должен быть пустым: " + tasks.isEmpty());
//        System.out.println();
//
//        System.out.println("Тест 2: Создание Task");
//        Task task1 = new Task("Имя", "Описание", Status.NEW);
//        Task task1Created = inMemoryTaskManager.createTask(task1);
//        System.out.println("Созданная Task должна содержать id: " + (task1Created.getId() != null));
//        System.out.println("Список Tasks должен содержать эту Task: " + inMemoryTaskManager.getTasks());
//        System.out.println();
//
//        System.out.println("Тест 3: Обновление Task");
//        Task task2 = new Task(task1Created.getId(), "Имя новое", "Описание новое", Status.IN_PROGRESS);
//        Task task2Updated = inMemoryTaskManager.updateTask(task2);
//        System.out.println("Обновленная Task должна иметь обновленные поля: " + task2Updated);
//        System.out.println();
//
//        System.out.println("Тест 4: Удаление Task");
//        inMemoryTaskManager.deleteTask(task2Updated.getId());
//        System.out.println("Список Tasks должен быть пустым: " + inMemoryTaskManager.getTasks());
//        System.out.println();
//    }
//
//    private static void testEpicsAndSubtasks() {
//        TaskManager inMemoryTaskManager = Managers.getInMemoryTaskManager(Managers.getDefaultHistory());
//
//        System.out.println("Тест 5: Пустые списки Epics и Subtasks");
//        List<Epic> epics = inMemoryTaskManager.getEpics();
//        List<Subtask> subtasks = inMemoryTaskManager.getSubtasks();
//        System.out.println("Список Epics и Subtasks должны быть пустые: " + epics.isEmpty() + " " + subtasks.isEmpty());
//        System.out.println();
//
//        System.out.println("Тест 6: Создание Epic");
//        Epic epic1 = new Epic("Имя эпика", "Описание эпика", Status.NEW);
//        Task epic1Created = inMemoryTaskManager.createEpic(epic1);
//        System.out.println("Созданный Epic должен содержать id: " + (epic1Created.getId() != null));
//        System.out.println("Список Epics должен содержать этот Epic: " + inMemoryTaskManager.getEpics());
//        System.out.println();
//
//        System.out.println("Тест 7: Создание Subtask1 в Epic");
//        Subtask subtask1 = new Subtask("Имя сабтаска1", "Описание сабтаска1",
//                Status.NEW, epic1Created.getId());
//        Task subtask1Created = inMemoryTaskManager.createSubtask(subtask1);
//        System.out.println("Созданный Subtask1 должен содержать id: " + (subtask1Created.getId() != null));
//        System.out.println("Список Subtasks должен содержать этот Subtask1: " + inMemoryTaskManager.getSubtasks());
//        System.out.println();
//
//        System.out.println("Тест 8: Создание Subtask2 в Epic");
//        Subtask subtask2 = new Subtask("Имя сабтаска2", "Описание сабтаска2",
//                Status.NEW, epic1Created.getId());
//        Task subtask2Created = inMemoryTaskManager.createSubtask(subtask2);
//        System.out.println("Созданный Subtask2 должен содержать id: " + (subtask2Created.getId() != null));
//        System.out.println("Список Subtasks должен содержать этот Subtask2 и остальные Subtask: "
//                + inMemoryTaskManager.getSubtasks());
//        System.out.println();
//
//        System.out.println("Тест 9: Обновление Subtask1, списка Subtasks и статуса Epic");
//        Subtask subtask3 = new Subtask(subtask1Created.getId(), "Имя Subtask новое",
//                "Описание Subtask новое",
//                Status.IN_PROGRESS, epic1Created.getId());
//        Subtask subtask3Updated = inMemoryTaskManager.updateSubtask(subtask3);
//        System.out.println("Обновленная Subtask3 должна иметь обновленные поля: " + subtask3Updated +
//                "Список Subtasks должен обновиться: " + inMemoryTaskManager.getSubtasks() +
//                "И статус Epic1 должен обновиться: " + epic1Created.getStatus());
//        System.out.println();
//
//        System.out.println("Тест 10: Удаление Subtask3, смена статусов Subtask2 и Epic");
//        inMemoryTaskManager.deleteSubtask(subtask3Updated.getId());
//        subtask2Created.setStatus(Status.DONE);
//        System.out.println("Обновляем статус Subtask2: " + subtask2Created.getStatus());
//        System.out.println("Список Subtasks должен содержать только один Subtask2: " + inMemoryTaskManager.getSubtasks());
//        Epic epic1Updated = inMemoryTaskManager.updateEpic(epic1);
//        System.out.println("Статус Epic также должен обновиться: " + epic1Updated.getStatus());
//        System.out.println();
//
//        System.out.println("Тест 11: Удаление Epic");
//        inMemoryTaskManager.deleteEpic(epic1Updated.getId());
//        System.out.println("Список Epics должен быть пустым: " + inMemoryTaskManager.getEpics());
//        System.out.println("Список Subtasks должен быть пустым: " + inMemoryTaskManager.getSubtasks());
//        System.out.println();
//    }
}
