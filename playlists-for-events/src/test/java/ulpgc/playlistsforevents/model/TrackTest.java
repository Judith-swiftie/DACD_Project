package ulpgc.playlistsforevents.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    @Test
    void trackStoresTitleCorrectly() {
        String title = "Imagine";
        Track track = new Track(title);
        assertEquals(title, track.getTitle(), "Track should return the correct title");
    }

    @Test
    void trackWithEmptyTitle() {
        Track track = new Track("");
        assertEquals("", track.getTitle(), "Track should return an empty string as title");
    }

    @Test
    void trackWithNullTitle() {
        Track track = new Track(null);
        assertNull(track.getTitle(), "Track should return null when title is null");
    }
}
