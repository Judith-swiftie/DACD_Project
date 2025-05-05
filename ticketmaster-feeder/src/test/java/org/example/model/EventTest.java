package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testEventCreation() {
        Artist artist1 = new Artist("Artista A");
        Artist artist2 = new Artist("Artista B");
        Event event = new Event("Concierto de Música", "2025-08-15", "21:00", "Auditorio Nacional", "Madrid", "España",
                Arrays.asList(artist1, artist2), "20 - 40 EUR");

        assertEquals("Concierto de Música", event.getName());
        assertEquals("2025-08-15", event.getDate());
        assertEquals("21:00", event.getTime());
        assertEquals("Auditorio Nacional", event.getVenue());
        assertEquals("Madrid", event.getCity());
        assertEquals("España", event.getCountry());
        assertEquals(2, event.getArtists().size());
        assertEquals("20 - 40 EUR", event.getPriceInfo());

        assertEquals("Artista A", event.getArtists().get(0).getName());
        assertEquals("Artista B", event.getArtists().get(1).getName());
    }

    @Test
    void testEventToString() {
        Artist artist1 = new Artist("Artista A");
        Artist artist2 = new Artist("Artista B");
        Event event = new Event("Concierto de Música", "2025-08-15", "21:00", "Auditorio Nacional", "Madrid", "España",
                Arrays.asList(artist1, artist2), "20 - 40 EUR");

        String expected = "🎫 Evento: Concierto de Música\n" +
                "📅 Fecha: 2025-08-15\n" +
                "⏰ Hora: 21:00\n" +
                "📍 Lugar: Auditorio Nacional\n" +
                "🏙️ Ciudad: Madrid\n" +
                "🌍 País: España\n" +
                "🎤 Artistas: Artista A, Artista B\n" +
                "💰 Precios: 20 - 40 EUR";

        assertEquals(expected, event.toString());
    }

    @Test
    void testEventToStringWithNoArtists() {
        Event event = new Event("Concierto de Música", "2025-08-15", "21:00", "Auditorio Nacional", "Madrid", "España",
                Arrays.asList(), "20 - 40 EUR");

        String expected = "🎫 Evento: Concierto de Música\n" +
                "📅 Fecha: 2025-08-15\n" +
                "⏰ Hora: 21:00\n" +
                "📍 Lugar: Auditorio Nacional\n" +
                "🏙️ Ciudad: Madrid\n" +
                "🌍 País: España\n" +
                "🎤 Artistas: No disponible\n" +
                "💰 Precios: 20 - 40 EUR";

        assertEquals(expected, event.toString());
    }
}
