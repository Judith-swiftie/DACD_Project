package ulpgc.ticketmaster.control.store;

import ulpgc.ticketmaster.model.Event;
import java.util.List;

public interface EventStore {
    void saveEvents(List<Event> events);
    List<Event> getAllEvents();
    Event findEventByName(String name);
    void deleteEventByName(String name);
    void updateEvent(Event event);
}
