package ru.practicum.tasktracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.tasktracker.exceptions.PriorityTaskException;
import ru.practicum.tasktracker.exceptions.TaskNotFoundException;
import ru.practicum.tasktracker.manager.Managers;
import ru.practicum.tasktracker.manager.TaskManager;
import ru.practicum.tasktracker.task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHttpHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public EpicsHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected void processGETRequest(HttpExchange exchange, Integer id) throws IOException {
        String response;
        try {
            if (id == null) {
                List<Epic> epics = taskManager.getEpics();
                response = Managers.getGson().toJson(epics);
            } else {
                Epic epic = taskManager.getEpic(id);
                if (subtasksInPath(exchange.getRequestURI().getPath())) {
                    response = Managers.getGson().toJson(epic.getSubtaskList());
                } else {
                    response = Managers.getGson().toJson(epic);
                }
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
        String epicString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = Managers.getGson().fromJson(epicString, Epic.class);
        try {
            if (epic.getId() == null) {
                taskManager.createEpic(epic);
            } else {
                taskManager.updateEpic(epic);
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
                taskManager.deleteEpics();
            } else {
                taskManager.deleteEpic(id);
            }
            sendText(exchange, "", SUCCESS_NO_DATA);
        } catch (Exception e) {
            sendText(exchange, e.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }

    protected boolean subtasksInPath(String path) {
        String[] parts = path.split("/");
        return parts.length >= 4 && parts[3].equals("subtasks");
    }
}