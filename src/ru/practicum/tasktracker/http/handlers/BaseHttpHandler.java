package ru.practicum.tasktracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected static final int SUCCESS = 200;

    protected static final int SUCCESS_NO_DATA = 201;

    protected static final int NOT_FOUND = 404;

    protected static final int NOT_ACCEPTABLE = 406;

    protected static final int INTERNAL_SERVER_ERROR = 500;

    protected static final int METHOD_NOT_ALLOWED = 405;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        switch (exchange.getRequestMethod()) {
            case "GET":
                processGETRequest(exchange, id);
                break;
            case "POST":
                processPOSTRequest(exchange);
                break;
            case "DELETE":
                processDELETERequest(exchange, id);
                break;
            default:
                System.out.println("Некорректный метод запроса: " + exchange.getRequestMethod());
        }
    }

    protected void sendText(HttpExchange httpExchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(statusCode, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected Integer getIdFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            return Integer.parseInt(parts[2]);
        }
        return null;
    }

    protected void processPOSTRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, "", METHOD_NOT_ALLOWED);
    }

    protected void processGETRequest(HttpExchange exchange, Integer id) throws IOException {
        sendText(exchange, "", METHOD_NOT_ALLOWED);
    }

    protected void processDELETERequest(HttpExchange exchange, Integer id) throws IOException {
        sendText(exchange, "", METHOD_NOT_ALLOWED);
    }
}