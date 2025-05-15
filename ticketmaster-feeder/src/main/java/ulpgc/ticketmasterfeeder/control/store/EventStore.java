package ulpgc.ticketmasterfeeder.control.store;

import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;

public interface EventStore {
    void saveEvents(List<Event> events);
    List<Event> getAllEvents();
}
