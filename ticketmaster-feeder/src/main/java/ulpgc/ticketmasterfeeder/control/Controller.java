package ulpgc.ticketmasterfeeder.control;

import ulpgc.ticketmasterfeeder.control.provider.TicketMasterEventProvider;
import ulpgc.ticketmasterfeeder.control.store.ActiveMQEventStore;
import ulpgc.ticketmasterfeeder.control.store.EventStore;
import ulpgc.ticketmasterfeeder.model.Event;

import java.util.List;

public class Controller {
    private static final String BROKER_URL = "tcp://localhost:61616";
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
