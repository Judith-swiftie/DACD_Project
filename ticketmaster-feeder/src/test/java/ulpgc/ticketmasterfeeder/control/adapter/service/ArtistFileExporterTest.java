package ulpgc.ticketmasterfeeder.control.adapter.service;

import org.junit.jupiter.api.*;
import ulpgc.ticketmasterfeeder.model.Artist;
import ulpgc.ticketmasterfeeder.model.Event;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistFileExporterTest {

    private static final String OUTPUT_FILE = "artists.txt";

    @BeforeEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get(OUTPUT_FILE));
    }

    @Test
    void exportArtistsFromEvents_createsFileWithUniqueArtists() throws IOException {
        Artist a1 = new Artist("Artist One");
        Artist a2 = new Artist("Artist Two");
        Artist a3 = new Artist("Artist Three");
        Event e1 = new Event("Event 1", "2025-05-17", "20:00", "Venue1", "City1", "Country1", List.of(a1, a2), "PriceInfo");
        Event e2 = new Event("Event 2", "2025-06-20", "21:00", "Venue2", "City2", "Country2", List.of(a2, a3), "PriceInfo");
        List<Event> events = List.of(e1, e2);
        ArtistFileExporter.exportArtistsFromEvents(events);
        File file = new File(OUTPUT_FILE);
        assertTrue(file.exists(), "El archivo no fue creado");
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(3, lines.size());
        assertTrue(lines.contains("Artist One"));
        assertTrue(lines.contains("Artist Two"));
        assertTrue(lines.contains("Artist Three"));
    }

    @Test
    void exportArtistsFromEvents_handlesEmptyEventList() throws IOException {
        ArtistFileExporter.exportArtistsFromEvents(List.of());
        File file = new File(OUTPUT_FILE);
        assertTrue(file.exists(), "El archivo no fue creado");
        List<String> lines = Files.readAllLines(file.toPath());
        assertTrue(lines.isEmpty(), "El archivo debería estar vacío");
    }
}
