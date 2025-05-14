package playlistsforevents.consumers;

import playlistsforevents.model.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class HistoricalEventLoader {

    private final String eventStorePath;

    public HistoricalEventLoader(String eventStorePath) {
        this.eventStorePath = eventStorePath;
    }

    public List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        Set<String> seenEventNames = new HashSet<>();

        try {
            long fileCount = Files.walk(Paths.get(eventStorePath))
                    .filter(path -> path.toString().endsWith(".events"))
                    .peek(file -> {
                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                try {
                                    Event event = Event.fromJson(line);
                                    if (event.getName() != null && seenEventNames.add(event.getName())) {
                                        events.add(event);
                                    }
                                } catch (Exception e) {
                                    System.err.println("Error parseando evento: " + e.getMessage());
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Error leyendo archivo: " + file + " - " + e.getMessage());
                        }
                    })
                    .count();

            System.out.println("Archivos .events procesados: " + fileCount);
        } catch (IOException e) {
            System.err.println("Error accediendo a archivos de eventos: " + e.getMessage());
        }

        System.out.println("Eventos únicos cargados: " + events.size());
        return events;
    }
}
