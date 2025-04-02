package org.example.control.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TicketmasterServiceTest {

    @Test
    public void testFetchMusicEvents() {
        TicketmasterService service = new TicketmasterService();
        List<Event> events = service.fetchMusicEvents();

        assertNotNull(events, "La lista de eventos no debe ser nula.");
        assertTrue(events.size() > 0, "Debe haber al menos un evento obtenido.");
    }

    @Test
    public void testParseEvent() {
        TicketmasterService service = new TicketmasterService();
        String json = "{ \"name\": \"Test Event\", \"dates\": { \"start\": { \"localDate\": \"2025-04-10\", \"localTime\": \"19:00\" } }, " +
                "\"_embedded\": { \"venues\": [{ \"name\": \"Test Venue\", \"city\": { \"name\": \"Test City\" }, \"country\": { \"name\": \"España\" } }] }, " +
                "\"classifications\": [{ \"segment\": { \"name\": \"Music\" } }], \"priceRanges\": [{ \"min\": 20.0, \"max\": 50.0, \"currency\": \"EUR\" }] }";

        try {
            // Crear un nodo JSON para la prueba
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode eventNode = objectMapper.readTree(json);

            // Acceder al método parseEvent a través de la reflexión
            Event event = service.parseEvent(eventNode);

            assertNotNull(event, "El evento no debe ser nulo.");
            assertEquals("Test Event", event.getName());
            assertEquals("2025-04-10", event.getDate());
            assertEquals("19:00", event.getTime());
            assertEquals("Test Venue", event.getVenue());
            assertEquals("Test City", event.getCity());
            assertEquals("España", event.getCountry());
            assertEquals("20.0 - 50.0 EUR", event.getPriceInfo());

        } catch (Exception e) {
            fail("Excepción al analizar el evento: " + e.getMessage());
        }
    }
}
