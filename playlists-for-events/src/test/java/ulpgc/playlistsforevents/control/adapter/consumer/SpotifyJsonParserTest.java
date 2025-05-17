package ulpgc.playlistsforevents.control.adapter.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SpotifyJsonParserTest {
    private SpotifyJsonParser parser;

    @BeforeEach
    void setUp() {
        parser = new SpotifyJsonParser();
    }

    @Test
    void testParseArtistName() {
        String jsonLine = """
            {
              "artistName": "Coldplay",
              "tracks": ["Yellow", "Fix You", "Viva La Vida"]
            }
            """;

        String result = parser.parseArtistName(jsonLine);
        assertEquals("Coldplay", result);
    }

    @Test
    void testParseTracks() {
        String jsonLine = """
            {
              "artistName": "Coldplay",
              "tracks": ["Yellow", "Fix You", "Viva La Vida"]
            }
            """;

        List<String> result = parser.parseTracks(jsonLine);
        assertEquals(List.of("Yellow", "Fix You", "Viva La Vida"), result);
    }

    @Test
    void testParseTracks_emptyList() {
        String jsonLine = """
            {
              "artistName": "Unknown",
              "tracks": []
            }
            """;

        List<String> result = parser.parseTracks(jsonLine);
        assertTrue(result.isEmpty());
    }
}
