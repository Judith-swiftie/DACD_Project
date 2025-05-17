package ulpgc.spotifyfeeder.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArtistTest {

    @Test
    void testArtistCreationAndGetters() {
        Artist artist = new Artist("123", "The Beatles");
        assertEquals("123", artist.getId());
        assertEquals("The Beatles", artist.getName());
    }

    @Test
    void testToStringContainsIdAndName() {
        Artist artist = new Artist("456", "Queen");
        String str = artist.toString();
        assertTrue(str.contains("456"));
        assertTrue(str.contains("Queen"));
        assertTrue(str.contains("Artist"));
    }
}
