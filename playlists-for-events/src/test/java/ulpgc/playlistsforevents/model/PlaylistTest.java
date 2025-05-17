package ulpgc.playlistsforevents.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    @Test
    void playlistStoresNameAndTracksCorrectly() {
        Track track1 = new Track("Song 1");
        Track track2 = new Track("Song 2");
        List<Track> tracks = Arrays.asList(track1, track2);
        String playlistName = "My Playlist";
        Playlist playlist = new Playlist(playlistName, tracks);
        assertEquals(playlistName, playlist.getName(), "Playlist name should match the one provided");
        assertEquals(tracks, playlist.getTracks(), "Playlist tracks should match the list provided");
    }

    @Test
    void playlistWithEmptyTrackList() {
        List<Track> emptyTracks = Collections.emptyList();
        Playlist playlist = new Playlist("Empty Playlist", emptyTracks);
        assertEquals("Empty Playlist", playlist.getName());
        assertTrue(playlist.getTracks().isEmpty(), "Playlist should have no tracks");
    }

    @Test
    void playlistWithNullName() {
        List<Track> tracks = Collections.singletonList(new Track("Song"));
        Playlist playlist = new Playlist(null, tracks);
        assertNull(playlist.getName(), "Playlist name should be null if set as null");
    }

    @Test
    void playlistWithNullTracks() {
        Playlist playlist = new Playlist("Null Tracks", null);
        assertEquals("Null Tracks", playlist.getName());
        assertNull(playlist.getTracks(), "Playlist tracks should be null if set as null");
    }
}
