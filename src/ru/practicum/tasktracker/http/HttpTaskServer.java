package ru.practicum.tasktracker.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.tasktracker.http.handlers.EpicsHttpHandler;
import ru.practicum.tasktracker.http.handlers.SubtasksHttpHandler;
import ru.practicum.tasktracker.http.handlers.TasksHttpHandler;
import ru.practicum.tasktracker.http.handlers.UserHttpHandler;
import ru.practicum.tasktracker.manager.Managers;
import ru.practicum.tasktracker.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.httpServer.createContext("/tasks", new TasksHttpHandler(taskManager));
        this.httpServer.createContext("/epics", new EpicsHttpHandler(taskManager));
        this.httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager));
        this.httpServer.createContext("/history", new UserHttpHandler(taskManager));
        this.httpServer.createContext("/prioritized", new UserHttpHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getInMemoryTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }
}