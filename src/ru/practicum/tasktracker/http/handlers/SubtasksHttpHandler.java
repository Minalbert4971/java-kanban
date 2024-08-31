package ru.practicum.tasktracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.tasktracker.exceptions.PriorityTaskException;
import ru.practicum.tasktracker.exceptions.TaskNotFoundException;
import ru.practicum.tasktracker.manager.Managers;
import ru.practicum.tasktracker.manager.TaskManager;
import ru.practicum.tasktracker.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHttpHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public SubtasksHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGETRequest(HttpExchange exchange, Integer id) throws IOException {
        String response;
        try {
            if (id == null) {
                List<Subtask> subtasks = taskManager.getSubtasks();
                response = Managers.getGson().toJson(subtasks);
            } else {
                Subtask subtask = taskManager.getSubtask(id);
                response = Managers.getGson().toJson(subtask);
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
        Subtask subtask = Managers.getGson().fromJson(taskString, Subtask.class);
        try {
            if (subtask.getId() == null) {
                taskManager.createSubtask(subtask);
            } else {
                taskManager.updateSubtask(subtask);
            }
            sendText(exchange, "", 201);
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
                taskManager.deleteSubtasks();
            } else {
                taskManager.deleteSubtask(id);
            }
            sendText(exchange, "", SUCCESS_NO_DATA);
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }
}