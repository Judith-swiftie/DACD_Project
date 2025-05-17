package ulpgc.playlistsforevents.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    void testEventStoresNameAndArtistsCorrectly() {
        Artist artist1 = new Artist("Taylor Swift");
        Artist artist2 = new Artist("Ed Sheeran");
        List<Artist> artists = Arrays.asList(artist1, artist2);
        Event event = new Event("Festival Pop", artists);
        assertEquals("Festival Pop", event.getName());
        assertEquals(2, event.getArtists().size());
        assertEquals("Taylor Swift", event.getArtists().get(0).getName());
        assertEquals("Ed Sheeran", event.getArtists().get(1).getName());
    }

    @Test
    void testToJsonProducesValidJson() {
        Artist artist = new Artist("Shakira");
        Event event = new Event("Concierto Único", List.of(artist));
        String json = event.toJson();
        assertTrue(json.contains("Concierto Único"));
        assertTrue(json.contains("Shakira"));
    }

    @Test
    void testFromJsonParsesCorrectly() {
        String json = """
            {
              "name": "Electro Fest",
              "artists": [
                { "name": "Daft Punk" },
                { "name": "Deadmau5" }
              ]
            }
            """;

        Event event = Event.fromJson(json);
        assertEquals("Electro Fest", event.getName());
        assertEquals(2, event.getArtists().size());
        assertEquals("Daft Punk", event.getArtists().get(0).getName());
        assertEquals("Deadmau5", event.getArtists().get(1).getName());
    }

    @Test
    void testJsonRoundTrip() {
        List<Artist> artists = List.of(new Artist("Björk"), new Artist("Massive Attack"));
        Event original = new Event("Alternativo Night", artists);
        String json = original.toJson();
        Event parsed = Event.fromJson(json);
        assertEquals(original.getName(), parsed.getName());
        assertEquals(original.getArtists().size(), parsed.getArtists().size());
        for (int i = 0; i < artists.size(); i++) {
            assertEquals(original.getArtists().get(i).getName(), parsed.getArtists().get(i).getName());
        }
    }
}
