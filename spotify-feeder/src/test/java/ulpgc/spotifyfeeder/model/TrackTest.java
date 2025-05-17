package ulpgc.spotifyfeeder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    @Test
    void testTrackCreationAndGetters() {
        Artist artist = new Artist("artist1", "Artist Name");
        Track track = new Track("track1", "Song Title", artist);
        assertEquals("track1", track.getId());
        assertEquals("Song Title", track.getName());
        assertEquals(artist, track.getArtist());
        assertEquals("Artist Name", track.getArtist().getName());
    }

    @Test
    void testToStringContainsAllFields() {
        Artist artist = new Artist("artist1", "Artist Name");
        Track track = new Track("track1", "Song Title", artist);
        String str = track.toString();
        assertTrue(str.contains("track1"));
        assertTrue(str.contains("Song Title"));
        assertTrue(str.contains("Artist Name"));
    }
}
