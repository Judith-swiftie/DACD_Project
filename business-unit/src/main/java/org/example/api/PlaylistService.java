package org.example.api;

import org.example.datamart.InMemoryDatamart;
import org.example.model.Event;
import org.example.model.Playlist;
import org.example.service.EventProcessor;
import org.example.service.PlaylistGenerator;

import java.util.List;
import java.util.Scanner;

public class PlaylistService {
    private final EventProcessor processor;
    private final PlaylistGenerator generator;
    private final InMemoryDatamart datamart;

    public PlaylistService(EventProcessor processor, PlaylistGenerator generator, InMemoryDatamart datamart) {
        this.processor = processor;
        this.generator = generator;
        this.datamart = datamart;
    }

    public void runCLI() {
        List<Event> events = processor.getValidEvents();
        if (events.isEmpty()) {
            System.out.println("No hay eventos válidos.");
            return;
        }

        System.out.println("Eventos disponibles:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println(i + ": " + events.get(i).getName());
        }

        System.out.print("Seleccione un evento: ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();

        Event selected = events.get(index);
        Playlist playlist = generator.createPlaylist(selected);
        datamart.store(playlist);

        System.out.println("Playlist generada: " + playlist.getName());
        playlist.getTracks().forEach(t -> System.out.println("- " + t.getTitle()));
    }
}

