package org.example;

import org.example.model.Event;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String eventStorePath = "eventstore/events/TicketmasterFeeder";
        String spotifyFeederPath = "eventstore/playlist/SpotifyFeeder";
        BusinessUnit unit = new BusinessUnit("events", eventStorePath, spotifyFeederPath);
        unit.loadHistoricalEvents();
        unit.loadSpotifyData();

        while (true) {
            System.out.println("\nMenú:");
            System.out.println("1. Buscar eventos por nombre");
            System.out.println("2. Buscar eventos por artista");
            System.out.println("3. Generar lista de reproducción desde todos los eventos");
            System.out.println("4. Salir");

            System.out.print("Elige una opción: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Introduce el nombre del evento: ");
                    String eventName = scanner.nextLine();
                    List<Event> eventsByName = unit.searchEventsByName(eventName);
                    handleEventSelection(eventsByName, unit, scanner);
                    break;
                case 2:
                    System.out.print("Introduce el nombre del artista: ");
                    String artistName = scanner.nextLine();
                    List<Event> eventsByArtist = unit.searchEventsByArtist(artistName);
                    handleEventSelection(eventsByArtist, unit, scanner);
                    break;
                case 3:
                    unit.interactWithUser();
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void handleEventSelection(List<Event> events, BusinessUnit unit, Scanner scanner) {
        if (events.isEmpty()) {
            System.out.println("No se encontraron eventos.");
            return;
        }
        System.out.println("Eventos encontrados:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i).getName());
        }
        System.out.print("Selecciona el número del evento para generar la playlist (o 0 para cancelar): ");
        int selection = scanner.nextInt();
        scanner.nextLine();

        if (selection < 1 || selection > events.size()) {
            System.out.println("Selección cancelada o inválida.");
            return;
        }
        Event selectedEvent = events.get(selection - 1);
        unit.generatePlaylistForEvent(selectedEvent);
    }
}
