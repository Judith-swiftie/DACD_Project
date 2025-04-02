package org.example.control.store;

import java.sql.SQLException;
import java.util.List;

public interface MusicStore {
    void createTables() throws SQLException;
    void saveArtistAndTracks(String artistId, String artistName, List<String> tracks) throws SQLException;
    List<String> getTracksByArtistId(String artistId) throws SQLException;
    boolean hasTracksChanged(String artistId, List<String> newTracks) throws SQLException;
}
