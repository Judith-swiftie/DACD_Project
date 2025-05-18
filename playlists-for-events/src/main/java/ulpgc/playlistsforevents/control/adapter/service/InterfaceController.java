package ulpgc.playlistsforevents.control.adapter.service;

import ulpgc.playlistsforevents.model.Event;
import java.util.List;
import java.util.Scanner;

public class InterfaceController {
    private final Controller controller;
    private final Scanner scanner;

    public InterfaceController(Controller controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void runUserInterface() {
        controller.loadHistoricalEvents();
        controller.loadSpotifyData();
        controller.start();
        while (true) {
            System.out.println("\nMenú:");
            System.out.println("1. Buscar eventos por nombre");
            System.out.println("2. Buscar eventos por artista");
            System.out.println("3. Buscar eventos desde la lista");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");
            int choice = readIntInput();
            switch (choice) {
                case 1 -> handleSearchByName();
                case 2 -> handleSearchByArtist();
                case 3 -> controller.interactWithUser();
                case 4 -> {
                    System.out.println("Saliendo...");
                    return;
                }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void handleSearchByName() {
        System.out.print("Introduce el nombre del evento: ");
        String eventName = scanner.nextLine();
        List<Event> eventsByName = controller.searchEventsByName(eventName);
        handleEventSelection(eventsByName);
    }

    private void handleSearchByArtist() {
        System.out.print("Introduce el nombre del artista: ");
        String artistName = scanner.nextLine();
        List<Event> eventsByArtist = controller.searchEventsByArtist(artistName);
        handleEventSelection(eventsByArtist);
    }

    private void handleEventSelection(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No se encontraron eventos.");
            return;
        }
        System.out.println("Eventos encontrados:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i).getName());
        }
        System.out.print("Selecciona el número del evento para generar la playlist (o 0 para cancelar): ");
        int selection = readIntInput();
        if (selection < 1 || selection > events.size()) {
            System.out.println("Selección cancelada o inválida.");
            return;
        }
        Event selectedEvent = events.get(selection - 1);
        controller.generatePlaylistForEvent(selectedEvent);
    }

    private int readIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida. Introduce un número: ");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }
}
