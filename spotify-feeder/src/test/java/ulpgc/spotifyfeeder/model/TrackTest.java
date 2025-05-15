package ulpgc.spotifyfeeder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrackTest {

    @Test
    public void testTrackAttributes() {
        Artist artist = new Artist("123", "Lady Gaga");
        Track track = new Track("1", "Shallow", artist);

        assertEquals("1", track.getId());
        assertEquals("Shallow", track.getName());
        assertEquals("Lady Gaga", track.getArtist().getName());
    }
}
