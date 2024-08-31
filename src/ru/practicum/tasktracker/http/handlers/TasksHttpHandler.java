package ru.practicum.tasktracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.tasktracker.exceptions.PriorityTaskException;
import ru.practicum.tasktracker.exceptions.TaskNotFoundException;
import ru.practicum.tasktracker.manager.Managers;
import ru.practicum.tasktracker.manager.TaskManager;
import ru.practicum.tasktracker.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHttpHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public TasksHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGETRequest(HttpExchange exchange, Integer id) throws IOException {
        String response;
        try {
            if (id == null) {
                List<Task> tasks = taskManager.getTasks();
                response = Managers.getGson().toJson(tasks);
            } else {
                Task task = taskManager.getTask(id);
                response = Managers.getGson().toJson(task);
            }
            sendText(exchange, response, SUCCESS);
        } catch (TaskNotFoundException e) {
            sendText(exchange, e.getMessage(), NOT_FOUND);
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void processPOSTRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = Managers.getGson().fromJson(taskString, Task.class);
        try {
            if (task.getId() == null) {
                taskManager.createTask(task);
            } else {
                taskManager.updateTask(task);
            }
            sendText(exchange, "", SUCCESS_NO_DATA);
        } catch (PriorityTaskException e) {
            sendText(exchange, "", NOT_ACCEPTABLE);
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void processDELETERequest(HttpExchange exchange, Integer id) throws IOException {
        try {
            if (id == null) {
                taskManager.deleteTasks();
            } else {
                taskManager.deleteTask(id);
            }
            sendText(exchange, "", SUCCESS_NO_DATA);
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }
}