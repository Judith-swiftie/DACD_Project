package org.example.control.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteMusicStoreTest {
    private SqliteMusicStore store;

    @BeforeEach
    public void setUp() throws SQLException {
        store = new SqliteMusicStore(System.getProperty("DB_URL"));
        store.createTables();
    }

    @Test
    public void testSaveArtistAndTracks() throws SQLException {
        store.saveArtistAndTracks("12345", "Lady Gaga", List.of("Shallow", "Bad Romance"));
        List<String> tracks = store.getTracksByArtistId("12345");
        assertEquals(2, tracks.size());
        assertTrue(tracks.contains("Shallow"));
        assertTrue(tracks.contains("Bad Romance"));
    }

    @Test
    public void testHasTracksChanged() throws SQLException {
        store.saveArtistAndTracks("12345", "Lady Gaga", List.of("Shallow"));
        boolean changed = store.hasTracksChanged("12345", List.of("Shallow", "Abracadabra"));
        assertTrue(changed);
    }
}
