package ulpgc.ticketmasterfeeder;

import ulpgc.ticketmasterfeeder.control.ArtistFileExporter;
import ulpgc.ticketmasterfeeder.control.Controller;
import ulpgc.ticketmasterfeeder.control.provider.TicketMasterEventProvider;
import ulpgc.ticketmasterfeeder.control.store.ActiveMQEventStore;
import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        ActiveMQEventStore eventStore = new ActiveMQEventStore(brokerUrl);
        TicketMasterEventProvider eventProvider = new TicketMasterEventProvider();
        Controller controller = new Controller(eventStore, eventProvider);
        System.out.println("Iniciando obtención y envío de eventos al broker...");
        controller.fetchAndSendEvents();
        List<Event> events = controller.getEventStore().getAllEvents();
        ArtistFileExporter.exportArtistsFromEvents(events);
    }
}
