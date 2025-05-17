package ulpgc.playlistsforevents.control.adapter.service;

import ulpgc.playlistsforevents.control.adapter.store.Datamart;
import ulpgc.playlistsforevents.model.Event;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class EventSelector {
    private final Datamart datamart;

    public EventSelector(Datamart datamart) {
        this.datamart = datamart;
    }

    public Optional<Event> selectEventFromUserInput() {
        List<Event> events = datamart.getAllEvents();
        if (events.isEmpty()) {informNoEventsAvailable();
            return Optional.empty();
        }
        displayEventOptions(events);
        int choice = readUserSelection();
        if (isValidChoice(choice, events.size())) {
            return Optional.of(events.get(choice - 1));
        } else {informInvalidSelection();
            return Optional.empty();
        }
    }

    private void informNoEventsAvailable() {
        System.out.println("No hay eventos disponibles.");
    }

    private void displayEventOptions(List<Event> events) {
        System.out.println("Eventos disponibles:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i).getName());
        }
        System.out.print("Selecciona el número del evento deseado: ");
    }

    private int readUserSelection() {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida. Introduce un número: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private boolean isValidChoice(int choice, int listSize) {
        return choice >= 1 && choice <= listSize;
    }

    private void informInvalidSelection() {
        System.out.println("Selección inválida.");
    }
}