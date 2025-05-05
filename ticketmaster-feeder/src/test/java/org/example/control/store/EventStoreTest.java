package org.example.control.store;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventStoreTest {

    @Test
    public void testEventStoreInterfaceMethods() {
        EventStore store = new SqliteEventStore();

        Event event = new Event("Electronic Night", "2025-08-25", "22:00", "Electro Dome", "Sevilla", "España", "DJ Electro", "20 - 40 EUR");
        store.saveEvents(List.of(event));

        List<Event> allEvents = store.getAllEvents();
        assertNotNull(allEvents);
        assertTrue(allEvents.size() > 0);

        Event foundEvent = store.findEventByName("Electronic Night");
        assertNotNull(foundEvent);
        assertEquals("Electronic Night", foundEvent.getName());

        store.deleteEventByName("Electronic Night");
        assertNull(store.findEventByName("Electronic Night"));
    }
}
