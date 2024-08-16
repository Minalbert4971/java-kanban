package ru.practicum.tasktracker.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter {

    private LocalDateAdapter() {
    }

    public static String formatter(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return localDateTime.format(formatter);
    }
}