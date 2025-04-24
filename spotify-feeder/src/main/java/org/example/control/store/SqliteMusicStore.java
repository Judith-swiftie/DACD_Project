package org.example.control.store;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SqliteMusicStore implements MusicStore {
    private final String url;

    public SqliteMusicStore(String url) {
        this.url = url;
        createTables();
    }

    public void createTables() {
        String createArtistsTable = "CREATE TABLE IF NOT EXISTS artists (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL)";

        String createTracksTable = "CREATE TABLE IF NOT EXISTS tracks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "artist_id TEXT NOT NULL, " +
                "track_name TEXT NOT NULL, " +
                "UNIQUE(artist_id, track_name), " +
                "FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE)";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.execute(createArtistsTable);
            statement.execute(createTracksTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(String artistId, String artistName, List<String> tracks) {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

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

            connection.commit();  // Commit de la transacción
        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de excepciones dentro del método
        }
    }

    @Override
    public List<String> getTracksByArtistId(String artistId) {
        List<String> tracks = new ArrayList<>();
        String query = "SELECT track_name FROM tracks WHERE artist_id = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, artistId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tracks.add(resultSet.getString("track_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    @Override
    public boolean hasTracksChanged(String artistId, List<String> newTracks) {
        List<String> existingTracks = getTracksByArtistId(artistId);
        return !new HashSet<>(existingTracks).equals(new HashSet<>(newTracks));  // Comparación de cambios
    }

    public void saveArtistAndTracks(String artistId, String artistName, List<String> tracks) {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);

            // Insertar o actualizar artista
            String artistQuery = "SELECT * FROM artists WHERE id = ?";
            try (PreparedStatement artistStatement = connection.prepareStatement(artistQuery)) {
                artistStatement.setString(1, artistId);
                ResultSet artistResult = artistStatement.executeQuery();

                // Si el artista no existe, insertarlo
                if (!artistResult.next()) {
                    String insertArtistQuery = "INSERT INTO artists (id, name) VALUES (?, ?)";
                    try (PreparedStatement insertArtistStatement = connection.prepareStatement(insertArtistQuery)) {
                        insertArtistStatement.setString(1, artistId);
                        insertArtistStatement.setString(2, artistName);
                        insertArtistStatement.executeUpdate();
                    }
                }
            }

            // Insertar o actualizar canciones
            for (String track : tracks) {
                String trackQuery = "SELECT * FROM tracks WHERE artist_id = ? AND track_name = ?";
                try (PreparedStatement trackStatement = connection.prepareStatement(trackQuery)) {
                    trackStatement.setString(1, artistId);
                    trackStatement.setString(2, track);
                    ResultSet trackResult = trackStatement.executeQuery();

                    // Si la canción no existe, insertarla
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

            connection.commit();  // Commit de la transacción
        } catch (SQLException e) {
            e.printStackTrace();  // Manejo de excepciones dentro del método
        }
    }
}
