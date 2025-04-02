package org.example.control.store;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SqliteMusicStore implements MusicStore {
    private static final String DB_URL = "jdbc:sqlite:database.db";

    @Override
    public void createTables() throws SQLException {
        String createArtistsTable = "CREATE TABLE IF NOT EXISTS artists (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL)";

        // Modificación para que la tabla 'tracks' tenga una clave compuesta (artist_id, track_name)
        String createTracksTable = "CREATE TABLE IF NOT EXISTS tracks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "artist_id TEXT NOT NULL, " +
                "track_name TEXT NOT NULL, " +
                "UNIQUE(artist_id, track_name), " +  // Clave compuesta única
                "FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(createArtistsTable);
            statement.execute(createTracksTable);
        }
    }
    @Override
    public void saveArtistAndTracks(String artistId, String artistName, List<String> tracks) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(false); // Empezamos una transacción

            // Primero, aseguramos que el artista está en la tabla 'artists'
            String artistQuery = "SELECT * FROM artists WHERE id = ?";
            try (PreparedStatement artistStatement = connection.prepareStatement(artistQuery)) {
                artistStatement.setString(1, artistId);
                ResultSet artistResult = artistStatement.executeQuery();

                if (!artistResult.next()) {
                    String insertArtistQuery = "INSERT INTO artists (id, name) VALUES (?, ?)";
                    try (PreparedStatement insertArtistStatement = connection.prepareStatement(insertArtistQuery)) {
                        insertArtistStatement.setString(1, artistId);
                        insertArtistStatement.setString(2, artistName);
                        insertArtistStatement.executeUpdate();
                    }
                }
            }

            for (String track : tracks) {
                String trackQuery = "SELECT * FROM tracks WHERE artist_id = ? AND track_name = ?";
                try (PreparedStatement trackStatement = connection.prepareStatement(trackQuery)) {
                    trackStatement.setString(1, artistId);
                    trackStatement.setString(2, track);
                    ResultSet trackResult = trackStatement.executeQuery();

                    if (!trackResult.next()) {
                        String insertTrackQuery = "INSERT INTO tracks (artist_id, track_name) VALUES (?, ?)";
                        try (PreparedStatement insertTrackStatement = connection.prepareStatement(insertTrackQuery)) {
                            insertTrackStatement.setString(1, artistId);
                            insertTrackStatement.setString(2, track);
                            insertTrackStatement.executeUpdate();
                        }
                    }
                }
            }

            connection.commit();
        }
    }


    @Override
    public List<String> getTracksByArtistId(String artistId) throws SQLException {
        String query = "SELECT track_name FROM tracks WHERE artist_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, artistId);
            ResultSet resultSet = statement.executeQuery();

            List<String> tracks = new ArrayList<>();
            while (resultSet.next()) {
                tracks.add(resultSet.getString("track_name"));
            }
            return tracks;
        }
    }

    @Override
    public boolean hasTracksChanged(String artistId, List<String> newTracks) throws SQLException {
        List<String> existingTracks = getTracksByArtistId(artistId);
        return !new HashSet<>(existingTracks).equals(new HashSet<>(newTracks));
    }
}
