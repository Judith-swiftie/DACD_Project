package ulpgc.playlistsforevents.control.adapter.consumer;

import ulpgc.playlistsforevents.control.port.EventParser;
import ulpgc.playlistsforevents.model.Event;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class HistoricalEventLoader {
    private final String eventStorePath;
    private final EventParser parser;

    public HistoricalEventLoader(String eventStorePath, EventParser parser) {
        this.eventStorePath = eventStorePath;
        this.parser = parser;
    }

    public List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        Set<String> seenEventNames = new HashSet<>();
        List<Path> eventFiles = getEventFiles();
        for (Path file : eventFiles) {
            List<Event> fileEvents = readEventsFromFile(file);
            for (Event event : fileEvents) {
                if (event.getName() != null && seenEventNames.add(event.getName())) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    private List<Path> getEventFiles() {
        try {
            return Files.walk(Paths.get(eventStorePath))
                    .filter(path -> path.toString().endsWith(".events"))
                    .toList();
        } catch (IOException e) {
            System.err.println("Error accediendo a archivos de eventos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Event> readEventsFromFile(Path file) {
        List<Event> events = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                parser.parse(line).ifPresent(events::add);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo: " + file + " - " + e.getMessage());
        }
        return events;
    }
}
