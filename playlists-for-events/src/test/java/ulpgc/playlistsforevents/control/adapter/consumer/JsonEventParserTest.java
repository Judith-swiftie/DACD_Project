package ulpgc.playlistsforevents.control.adapter.consumer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ulpgc.playlistsforevents.model.Event;
import java.util.Optional;

public class JsonEventParserTest {
    private final JsonEventParser parser = new JsonEventParser();

    @Test
    public void parse_validJson_returnsEventOptional() {
        String json = "{\"name\":\"Concert\",\"artists\":[]}";
        Optional<Event> result = parser.parse(json);
        assertTrue(result.isPresent(), "Expected non-empty Optional for valid JSON");
        assertEquals("Concert", result.get().getName());
    }


    @Test
    public void fromJson_validJson_returnsEvent() {
        String json = "{\"name\":\"Concert\",\"artists\":[]}";
        Event event = parser.fromJson(json);
        assertEquals("Concert", event.getName());
        assertNotNull(event.getArtists());
        assertTrue(event.getArtists().isEmpty());
    }

}
