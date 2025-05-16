package ulpgc.ticketmasterfeeder.control.port;

import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;

public interface EventStore {
    void saveEvents(List<Event> events);
    List<Event> getAllEvents();
}
