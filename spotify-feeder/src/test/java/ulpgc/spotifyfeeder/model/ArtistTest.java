package ulpgc.spotifyfeeder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistTest {

    @Test
    public void testArtistAttributes() {
        Artist artist = new Artist("123", "Lady Gaga");
        assertEquals("123", artist.getId());
        assertEquals("Lady Gaga", artist.getName());
    }
}
