package ulpgc.ticketmasterfeeder.control.adapter.service;

import ulpgc.ticketmasterfeeder.control.adapter.provider.TicketMasterEventProvider;
import ulpgc.ticketmasterfeeder.control.adapter.store.ActiveMQEventStore;
import ulpgc.ticketmasterfeeder.control.port.EventStore;
import ulpgc.ticketmasterfeeder.model.Event;

import java.util.List;

public class Controller {
    private final ActiveMQEventStore eventStore;
    private final TicketMasterEventProvider eventProvider;

    public Controller(ActiveMQEventStore eventStore, TicketMasterEventProvider eventProvider) {
        this.eventStore = eventStore;
        this.eventProvider = eventProvider;
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
