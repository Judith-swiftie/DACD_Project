package org.example.control;

import org.example.control.provider.TicketMasterEventProvider;
import org.example.control.store.ActiveMQEventStore;
import org.example.control.store.EventStore;
import org.example.model.Event;

import java.util.List;

public class Controller {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private final ActiveMQEventStore eventStore;
    private final TicketMasterEventProvider eventProvider;

    public Controller() {
        this.eventStore = new ActiveMQEventStore(BROKER_URL);
        this.eventProvider = new TicketMasterEventProvider();
    }
    public EventStore getEventStore() {
        return this.eventStore;
    }

    public void fetchAndSendEvents() {
        List<Event> events = eventProvider.fetchMusicEvents();
        if (events.isEmpty()) {
            System.out.println("No se encontraron eventos.");
        } else {
            eventStore.saveEvents(events);
        }
    }

}
