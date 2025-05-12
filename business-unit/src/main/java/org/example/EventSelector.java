package org.example;

import org.example.datamart.Datamart;
import org.example.model.Event;

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
        if (events.isEmpty()) {
            System.out.println("No hay eventos disponibles.");
            return Optional.empty();
        }
        System.out.println("Eventos disponibles:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + events.get(i).getName());
        }
        System.out.print("Selecciona el número del evento deseado: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice < 1 || choice > events.size()) {
            System.out.println("Selección inválida.");
            return Optional.empty();
        }
        return Optional.of(events.get(choice - 1));
    }
}
