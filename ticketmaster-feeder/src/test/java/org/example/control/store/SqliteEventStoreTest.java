package org.example.control.store;

import org.example.model.Artist;
import org.example.model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqliteEventStoreTest {

    private SqliteEventStore eventStore;
    private static final String DB_URL = "jdbc:sqlite::memory:"; // Base de datos en memoria para pruebas

    @BeforeEach
    void setUp() {
        System.setProperty("DB_URL", DB_URL);
        eventStore = new SqliteEventStore();
    }

    @AfterEach
    void tearDown() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.createStatement().execute("DROP TABLE IF EXISTS events");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveAndGetEvents() {
        Event event = new Event("Concierto de Rock", "2025-05-15", "20:00", "Auditorio", "Madrid", "España",
                Arrays.asList(new Artist("Banda 1"), new Artist("Banda 2")), "20 - 40 EUR");

        eventStore.saveEvents(List.of(event));

        List<Event> events = eventStore.getAllEvents();
        assertEquals("Concierto de Rock", events.get(0).getName());
        assertEquals("2025-05-15", events.get(0).getDate());
    }

    @Test
    void testEventExists() {
        Event event = new Event("Concierto de Pop", "2025-06-20", "19:00", "Teatro", "Barcelona", "España",
                Arrays.asList(new Artist("Banda A")), "25 - 50 EUR");

        eventStore.saveEvents(List.of(event));
        Event foundEvent = eventStore.findEventByName("Concierto de Pop");

        assertNotNull(foundEvent);
        assertEquals("Concierto de Pop", foundEvent.getName());
    }

    @Test
    void testDeleteEvent() {
        Event event = new Event("Concierto de Jazz", "2025-07-10", "21:00", "Sala de Jazz", "Sevilla", "España",
                Arrays.asList(new Artist("Banda X")), "30 - 60 EUR");

        eventStore.saveEvents(List.of(event));
        eventStore.deleteEventByName("Concierto de Jazz");

        Event foundEvent = eventStore.findEventByName("Concierto de Jazz");
        assertNull(foundEvent);
    }
}
