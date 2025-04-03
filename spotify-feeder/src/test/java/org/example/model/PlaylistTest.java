package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {

    @Test
    public void testPlaylistAttributes() {
        Track track = new Track("1", "Shallow", new Artist("123", "Lady Gaga"));
        Playlist playlist = new Playlist("My Playlist", List.of(track));

        assertEquals("My Playlist", playlist.getName());
        assertEquals(1, playlist.getTracks().size());
        assertEquals("Shallow", playlist.getTracks().get(0).getName());
    }
}
