package ulpgc.ticketmasterfeeder.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    void testEventConstructorAndGetters() {
        Artist artist1 = new Artist("Artist A");
        Artist artist2 = new Artist("Artist B");
        Event event = new Event(
                "Concert",
                "2025-06-01",
                "20:00",
                "Gran Arena",
                "Madrid",
                "España",
                List.of(artist1, artist2),
                "30 - 50 EUR"
        );

        assertEquals("Concert", event.getName());
        assertEquals("2025-06-01", event.getDate());
        assertEquals("20:00", event.getTime());
        assertEquals("Gran Arena", event.getVenue());
        assertEquals("Madrid", event.getCity());
        assertEquals("España", event.getCountry());
        assertEquals(2, event.getArtists().size());
        assertEquals("Artist A", event.getArtists().getFirst().getName());
        assertEquals("30 - 50 EUR", event.getPriceInfo());
    }

    @Test
    void testToStringIncludesAllDetails() {
        Artist artist = new Artist("Juanes");
        Event event = new Event(
                "Fiesta Latina",
                "2025-08-10",
                "21:00",
                "Auditorio Sur",
                "Sevilla",
                "España",
                List.of(artist),
                "25 EUR"
        );

        String toString = event.toString();

        assertTrue(toString.contains("Evento: Fiesta Latina"));
        assertTrue(toString.contains("Fecha: 2025-08-10"));
        assertTrue(toString.contains("Hora: 21:00"));
        assertTrue(toString.contains("Lugar: Auditorio Sur"));
        assertTrue(toString.contains("Ciudad: Sevilla"));
        assertTrue(toString.contains("País: España"));
        assertTrue(toString.contains("Artistas: Juanes"));
        assertTrue(toString.contains("Precios: 25 EUR"));
    }
}
