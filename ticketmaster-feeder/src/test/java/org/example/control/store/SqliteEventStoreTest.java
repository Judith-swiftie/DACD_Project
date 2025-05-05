package org.example.control.store;

import org.example.model.Event;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteEventStoreTest {

    @Test
    public void testSaveEvents() {
        SqliteEventStore store = new SqliteEventStore();
        List<Event> events = new ArrayList<>();
        events.add(new Event("Indie Night", "2025-05-15", "21:00", "Indie Club", "Valencia", "España", "The Indies", "10 - 20 EUR"));

        assertDoesNotThrow(() -> store.saveEvents(events), "Guardar eventos no debe lanzar excepciones.");
    }

    @Test
    public void testGetAllEvents() {
        SqliteEventStore store = new SqliteEventStore();
        List<Event> events = store.getAllEvents();

        assertNotNull(events, "La lista de eventos no debe ser nula.");
    }

    @Test
    public void testFindEventByName() {
        SqliteEventStore store = new SqliteEventStore();
        Event event = store.findEventByName("Indie Night");

        assertNotNull(event, "El evento debe existir en la base de datos.");
        assertEquals("Indie Night", event.getName());
    }

    @Test
    public void testDeleteEventByName() {
        SqliteEventStore store = new SqliteEventStore();
        store.deleteEventByName("Indie Night");

        Event event = store.findEventByName("Indie Night");
        assertNull(event, "El evento debería haber sido eliminado.");
    }
}
