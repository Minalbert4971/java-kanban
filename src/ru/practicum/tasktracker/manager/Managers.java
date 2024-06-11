package ru.practicum.tasktracker.manager;

public class Managers {

    private Managers() {}

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}