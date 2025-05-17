package ulpgc.spotifyfeeder.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    @Test
    void testPlaylistCreationAndGetters() {
        Artist artist = new Artist("artist1", "Artist Name");
        Track track1 = new Track("1", "Song 1", artist);
        Track track2 = new Track("2", "Song 2", artist);
        Playlist playlist = new Playlist("My Playlist", List.of(track1, track2));
        assertEquals("My Playlist", playlist.getName());
        assertNotNull(playlist.getTracks());
        assertEquals(2, playlist.getTracks().size());
        assertTrue(playlist.getTracks().contains(track1));
        assertTrue(playlist.getTracks().contains(track2));
    }

    @Test
    void testToStringContainsNameAndTracks() {
        Artist artist = new Artist("artist1", "Artist Name");
        Track track = new Track("1", "Song 1", artist);
        Playlist playlist = new Playlist("Playlist Test", List.of(track));
        String str = playlist.toString();
        assertTrue(str.contains("Playlist Test"));
        assertTrue(str.contains("Song 1"));
        assertTrue(str.contains("Artist Name"));
    }
}
