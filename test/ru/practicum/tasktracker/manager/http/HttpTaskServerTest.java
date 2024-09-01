package ru.practicum.tasktracker.manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.tasktracker.enums.Status;
import ru.practicum.tasktracker.exceptions.TaskNotFoundException;
import ru.practicum.tasktracker.http.HttpTaskServer;
import ru.practicum.tasktracker.http.adapter.DurationAdapter;
import ru.practicum.tasktracker.http.adapter.LocalDateTimeAdapter;
import ru.practicum.tasktracker.manager.HistoryManager;
import ru.practicum.tasktracker.manager.Managers;
import ru.practicum.tasktracker.manager.TaskManager;
import ru.practicum.tasktracker.task.Epic;
import ru.practicum.tasktracker.task.Subtask;
import ru.practicum.tasktracker.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpTaskServerTest {

    HttpTaskServer server;
    TaskManager taskManager;

    HistoryManager historyManager;

    Task task1;
    Task task2;
    Subtask subtask3;
    Subtask subtask4;
    Epic epic5;
    Epic epic6;
    private HttpClient client;
    Gson gson;

    @BeforeEach
    public void setup() throws IOException {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getInMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        gson = Managers.getGson();
        client = HttpClient.newHttpClient();

        task1 = new Task(1, "Задача 1", "description1", Status.NEW,
                LocalDateTime.of(2024, 8, 25, 9, 0, 0),
                Duration.ofMinutes(10));
        task2 = new Task(2, "Задача 2", "description2", Status.NEW,
                LocalDateTime.of(2024, 8, 26, 9, 0, 0),
                Duration.ofMinutes(10));

        subtask3 = new Subtask(3, "Подзадача3", "description3", Status.NEW,
                LocalDateTime.of(2024, 8, 30, 9, 0, 0),
                Duration.ofMinutes(10), 5);
        subtask4 = new Subtask(4, "Подзадача4", "description4", Status.NEW,
                LocalDateTime.of(2024, 8, 31, 9, 0, 0),
                Duration.ofMinutes(10), 6);

        epic5 = new Epic(5, "Эпик5", "description5", Status.NEW);
        epic6 = new Epic(6, "Эпик6", "description6", Status.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.createEpic(epic5);
        taskManager.createEpic(epic6);

        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        epic5.addSubtask(subtask3);
        epic6.addSubtask(subtask4);

        server.start();
    }

    @AfterEach
    public void end() {
        server.stop();
    }

    @Test
    void testGetGson() {
        GsonBuilder expectedBuilder = new GsonBuilder();
        expectedBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        expectedBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        Gson expectedGson = expectedBuilder.create();

        assertEquals(expectedGson.toJson(
                        LocalDateTime.of(2024, 8, 30, 9, 0, 0)),
                gson.toJson(LocalDateTime.of(2024, 8, 30, 9, 0, 0)));
        assertEquals(expectedGson.toJson(Duration.ofMinutes(10)), gson.toJson(Duration.ofMinutes(10)));
    }

    @Test
    void testGetTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(taskManager.getTasks()), response.body());
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(task1), response.body());
    }

    @Test
    void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задача с данным ID не найдена", response.body());
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testCreateTaskWithIntersect() throws IOException, InterruptedException {
        Task task3 = new Task(10, "Задача 3", "description3", Status.NEW,
                LocalDateTime.of(2024, 8, 18, 12, 0, 0),
                Duration.ofMinutes(10));
        taskManager.createTask(task3);
        String taskJson1 = gson.toJson(task3);
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        Task task4 = new Task(11, "Задача 4", "description4", Status.NEW,
                LocalDateTime.of(2024, 8, 19, 12, 0, 0),
                Duration.ofMinutes(10));
        taskManager.createTask(task4);
        task4.setStartTime(LocalDateTime.of(2024, 8, 18, 12, 0, 0));
        String taskJson2 = gson.toJson(task4);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        task1.setId(9);
        taskManager.createTask(task1);

        taskManager.updateTask(task1);
        String taskJson = gson.toJson(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateTaskWithIntersect() throws IOException, InterruptedException {
        Task task3 = new Task(8, "Задача 1", "description1", Status.NEW,
                LocalDateTime.of(2024, 8, 19, 12, 0, 0),
                Duration.ofMinutes(10));
        task3.setStartTime(LocalDateTime.of(2024, 8, 1, 12, 0, 0));
        task3.setDuration(Duration.ofMinutes(10));
        taskManager.createTask(task3);

        task1.setStartTime(LocalDateTime.of(2024, 8, 19, 12, 0, 0));
        task1.setDuration(Duration.ofMinutes(10));

        task2.setStartTime(LocalDateTime.of(2024, 8, 19, 12, 5, 0));
        task2.setDuration(Duration.ofMinutes(10));

        String taskJson1 = gson.toJson(task3);
        String taskJson2 = gson.toJson(task1);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());
        assertEquals(406, response2.statusCode());
    }

    @Test
    void testDeleteTaskById() throws IOException, InterruptedException {
        int taskId = 1;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(taskId));
    }

    @Test
    void testDeleteAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void testGetSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(taskManager.getSubtasks()), response.body());
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/3"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(subtask3), response.body());
    }

    @Test
    void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Подзадача с данным ID не найдена", response.body());
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(subtask3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testCreateSubtaskWithIntersect() throws IOException, InterruptedException {
        Subtask subtask7 = new Subtask(7, "Подзадача 7", "description7", Status.NEW,
                LocalDateTime.of(2024, 7, 25, 9, 0, 0),
                Duration.ofMinutes(10), 5);
        taskManager.createSubtask(subtask7);

        Subtask subtask8 = new Subtask(8, "Подзадача 8", "description8", Status.NEW,
                LocalDateTime.of(2024, 9, 25, 9, 0, 0),
                Duration.ofMinutes(10), 5);
        taskManager.createSubtask(subtask8);

        String taskJson1 = gson.toJson(subtask7);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        subtask8.setStartTime(LocalDateTime.of(2024, 7, 25, 9, 0, 0));
        String taskJson2 = gson.toJson(subtask8);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        subtask3.setId(4);
        String taskJson = gson.toJson(subtask3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateSubtaskWithIntersect() throws IOException, InterruptedException {
        Subtask subtask15 = new Subtask(15, "Задача 15", "description15", Status.NEW,
                LocalDateTime.of(2024, 8, 19, 12, 0, 0),
                Duration.ofMinutes(10), 6);
        subtask15.setStartTime(LocalDateTime.of(2024, 8, 1, 12, 0, 0));
        subtask15.setDuration(Duration.ofMinutes(10));
        taskManager.createSubtask(subtask15);

        subtask3.setStartTime(LocalDateTime.of(2024, 8, 18, 12, 0, 0));
        subtask3.setDuration(Duration.ofMinutes(10));

        subtask4.setStartTime(LocalDateTime.of(2024, 8, 18, 12, 5, 0));
        subtask4.setDuration(Duration.ofMinutes(10));

        String taskJson1 = gson.toJson(subtask15);
        String taskJson2 = gson.toJson(subtask4);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());
        assertEquals(406, response2.statusCode());
    }

    @Test
    void testDeleteSubtaskById() throws IOException, InterruptedException {
        int subtaskId = 4;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtaskId))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> taskManager.getSubtask(subtaskId));
    }

    @Test
    void testDeleteAllSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(taskManager.getEpics()), response.body());
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/5"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(epic5), response.body());
    }

    @Test
    void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Эпик с данным ID не найден", response.body());
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        String taskJson = gson.toJson(epic5);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        String taskJson = gson.toJson(epic5);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void testDeleteEpicById() throws IOException, InterruptedException {
        int epicId = 5;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> taskManager.getEpic(epicId));
    }

    @Test
    void testDeleteAllEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    void testGetEpicSubtaskMissingEpic() throws IOException, InterruptedException {
        int epicId = 500;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void testPrioritisedTasks() throws IOException, InterruptedException {
        taskManager.clearPrioritizedTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteTasks();

        task1 = new Task(1, "Задача 1", "description1", Status.NEW,
                LocalDateTime.of(2024, 8, 25, 9, 0, 0),
                Duration.ofMinutes(10));
        task2 = new Task(2, "Задача 2", "description2", Status.NEW,
                LocalDateTime.of(2024, 8, 30, 9, 0, 0),
                Duration.ofMinutes(10));
        subtask3 = new Subtask(3, "Подзадача3", "description3", Status.NEW,
                LocalDateTime.of(2024, 8, 26, 9, 0, 0),
                Duration.ofMinutes(10), 5);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createSubtask(subtask3);

        task1.setStartTime(LocalDateTime.of(2024, 8, 18, 12, 0, 0));
        task1.setDuration(Duration.ofMinutes(10));

        task2.setStartTime(LocalDateTime.of(2024, 8, 18, 12, 45, 0));
        task2.setDuration(Duration.ofMinutes(30));

        subtask3.setStartTime(LocalDateTime.of(2024, 8, 18, 12, 15, 0));
        subtask3.setDuration(Duration.ofMinutes(15));

        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(subtask3);
        tasks.add(task2);

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(subtask3);
        String taskJson3 = gson.toJson(task2);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3))
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        client.send(request3, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(gson.toJson(tasks), response.body());
    }

    @Test
    void testHistoryTasks() throws IOException, InterruptedException {
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(subtask4);
        tasks.add(task2);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/1"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/4"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/2"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.send(request1, HttpResponse.BodyHandlers.ofString());
        client.send(request2, HttpResponse.BodyHandlers.ofString());
        client.send(request3, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(gson.toJson(tasks), response.body());
    }
}