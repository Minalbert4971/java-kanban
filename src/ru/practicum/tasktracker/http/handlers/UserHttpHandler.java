package ru.practicum.tasktracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.tasktracker.manager.Managers;
import ru.practicum.tasktracker.manager.TaskManager;
import ru.practicum.tasktracker.task.Task;

import java.io.IOException;
import java.util.List;

public class UserHttpHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public UserHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        List<Task> tasks;
        String command = getCommand(exchange.getRequestURI().getPath());

        switch (command) {
            case "history":
                tasks = taskManager.getHistory();
                response = Managers.getGson().toJson(tasks);
                break;
            case "prioritized":
                tasks = taskManager.getPrioritizedTasks();
                response = Managers.getGson().toJson(tasks);
                break;
            default:
                System.out.println("Some error appeared...");
        }

        sendText(exchange, response, 200);
    }

    protected String getCommand(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 2) {
            return parts[1];
        }
        return null;
    }
}