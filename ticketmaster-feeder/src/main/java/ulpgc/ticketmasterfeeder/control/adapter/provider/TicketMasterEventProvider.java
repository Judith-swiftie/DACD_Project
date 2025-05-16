package ulpgc.ticketmasterfeeder.control.adapter.provider;

import ulpgc.ticketmasterfeeder.control.port.MusicalEventProvider;
import ulpgc.ticketmasterfeeder.model.Event;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventProvider implements MusicalEventProvider {
    private final TicketMasterClient client;
    private final TicketMasterEventParser parser;
    private static final int TOTAL_PAGES = 20;

    public TicketMasterEventProvider(TicketMasterClient client, TicketMasterEventParser parser) {
        this.client = client;
        this.parser = parser;
    }

    @Override
    public List<Event> fetchMusicEvents() {
        List<Event> events = new ArrayList<>();
        for (int page = 0; page < TOTAL_PAGES; page++) {
            try {
                String body = client.fetchPage(page);
                if (body == null) {
                    page--;
                    continue;
                }
                events.addAll(parser.parseEventsFromBody(body));
            } catch (Exception e) {
                System.err.println("Error en página " + page + ": " + e.getMessage());
            }
        }
        return events;
    }
}
