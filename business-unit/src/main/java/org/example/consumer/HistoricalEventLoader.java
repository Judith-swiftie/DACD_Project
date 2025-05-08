package org.example.consumer;

import org.example.model.Event;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class HistoricalEventLoader {

    private final String eventStorePath;

    public HistoricalEventLoader(String eventStorePath) {
        this.eventStorePath = eventStorePath;
    }

    public List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        File[] files = new File(eventStorePath).listFiles((dir, name) -> name.endsWith(".events"));

        if (files != null) {
            for (File file : files) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()));
                    Event event = Event.fromJson(json); // Suponiendo que tengas un método de deserialización
                    events.add(event);
                } catch (IOException e) {
                    System.err.println("Error leyendo el archivo de eventos: " + e.getMessage());
                }
            }
        }
        return events;
    }
}

