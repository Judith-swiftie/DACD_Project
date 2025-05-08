package org.example.integration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.Artist;
import org.example.model.Event;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class TicketmasterReader {
    private final Path basePath = Paths.get("eventstore/events");
    private final Gson gson = new Gson();

    public List<Event> loadAllEvents() {
        List<Event> events = new ArrayList<>();
        try (DirectoryStream<Path> ssDirs = Files.newDirectoryStream(basePath)) {
            for (Path ssDir : ssDirs) {
                try (DirectoryStream<Path> files = Files.newDirectoryStream(ssDir, "*.events")) {
                    for (Path file : files) {
                        events.addAll(readEventsFromFile(file));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading event files: " + e.getMessage());
        }
        return events;
    }

    private List<Event> readEventsFromFile(Path file) {
        List<Event> events = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file);
            for (String json : lines) {
                Event event = parseEvent(json);
                if (event != null) {
                    events.add(event);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + file + ": " + e.getMessage());
        }
        return events;
    }

    private Event parseEvent(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = gson.fromJson(json, mapType);
            String id = (String) data.get("id");
            String name = (String) data.get("name");
            List<Map<String, String>> rawArtists = (List<Map<String, String>>) data.get("artists");
            List<Artist> artists = new ArrayList<>();
            for (Map<String, String> a : rawArtists) {
                artists.add(new Artist(a.get("name"), null));
            }
            return new Event(id, name, artists);
        } catch (Exception e) {
            System.err.println("Error parsing event JSON: " + e.getMessage());
            return null;
        }
    }
}

