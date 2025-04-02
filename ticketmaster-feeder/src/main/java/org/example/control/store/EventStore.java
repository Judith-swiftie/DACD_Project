package org.example.control.store;

import org.example.control.provider.Event;
import java.util.List;

public interface EventStore {
    void saveEvents(List<Event> events);
    List<Event> getAllEvents();
    Event findEventByName(String name);
    void deleteEventByName(String name);
    void updateEvent(Event event);
}
