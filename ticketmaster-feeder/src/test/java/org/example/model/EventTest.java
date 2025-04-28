package org.example.model;

import org.example.control.provider.Event;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    public void testEventConstructor() {
        Event event = new Event("Rock Fest", "2025-06-15", "20:00", "Rock Arena", "Madrid", "España", "The Rockers", "30 - 50 EUR");

        assertEquals("Rock Fest", event.getName());
        assertEquals("2025-06-15", event.getDate());
        assertEquals("20:00", event.getTime());
        assertEquals("Rock Arena", event.getVenue());
        assertEquals("Madrid", event.getCity());
        assertEquals("España", event.getCountry());
        assertEquals("The Rockers", event.getArtists());
        assertEquals("30 - 50 EUR", event.getPriceInfo());
    }

    @Test
    public void testPrintDetails() {
        Event event = new Event("Jazz Night", "2025-04-10", "19:00", "Jazz Club", "Barcelona", "España", "The Jazz Band", "20 - 40 EUR");
        assertDoesNotThrow(event::printDetails, "El método printDetails no debería lanzar excepciones.");
    }
}
