package ulpgc.ticketmasterfeeder;

import ulpgc.ticketmasterfeeder.control.adapter.service.ArtistFileExporter;
import ulpgc.ticketmasterfeeder.control.adapter.service.Controller;
import ulpgc.ticketmasterfeeder.control.adapter.provider.TicketMasterClient;
import ulpgc.ticketmasterfeeder.control.adapter.provider.TicketMasterEventParser;
import ulpgc.ticketmasterfeeder.control.adapter.provider.TicketMasterEventProvider;
import ulpgc.ticketmasterfeeder.control.adapter.store.ActiveMQEventStore;
import ulpgc.ticketmasterfeeder.model.Event;
import java.net.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        TicketMasterClient client = new TicketMasterClient(httpClient);
        TicketMasterEventParser parser = new TicketMasterEventParser(objectMapper);
        TicketMasterEventProvider eventProvider = new TicketMasterEventProvider(client, parser);
        ActiveMQEventStore eventStore = new ActiveMQEventStore(brokerUrl);
        Controller controller = new Controller(eventStore, eventProvider);
        System.out.println("Iniciando obtención y envío de eventos al broker...");
        controller.fetchAndSendEvents();
        List<Event> events = controller.getEventStore().getAllEvents();
        ArtistFileExporter.exportArtistsFromEvents(events);
    }
}
