package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpotifyDatabaseService {
    private static final String DB_URL = "jdbc:sqlite:spotify.db";

    // Crear las tablas en la base de datos si no existen
    public static void createTables() throws SQLException {
        String createArtistsTable = "CREATE TABLE IF NOT EXISTS artists (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL)";

        String createTracksTable = "CREATE TABLE IF NOT EXISTS tracks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "artist_id TEXT NOT NULL, " +
                "track_name TEXT NOT NULL, " +
                "FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE)";

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(createArtistsTable);
            statement.execute(createTracksTable);
        }
    }

    // Guardar el artista y sus canciones, solo si hay cambios
    public static void saveArtistAndTracks(String artistId, String artistName, List<String> tracks) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            connection.setAutoCommit(false);

            // Verificar si el artista ya está almacenado
            String artistQuery = "SELECT * FROM artists WHERE id = ?";
            try (PreparedStatement artistStatement = connection.prepareStatement(artistQuery)) {
                artistStatement.setString(1, artistId);
                ResultSet artistResult = artistStatement.executeQuery();

                if (!artistResult.next()) {
                    // Si el artista no está almacenado, insertarlo
                    String insertArtistQuery = "INSERT INTO artists (id, name) VALUES (?, ?)";
                    try (PreparedStatement insertArtistStatement = connection.prepareStatement(insertArtistQuery)) {
                        insertArtistStatement.setString(1, artistId);
                        insertArtistStatement.setString(2, artistName);
                        insertArtistStatement.executeUpdate();
                    }
                }
            }

            // Guardar canciones solo si son nuevas o han cambiado
            for (String track : tracks) {
                String trackQuery = "SELECT * FROM tracks WHERE artist_id = ? AND track_name = ?";
                try (PreparedStatement trackStatement = connection.prepareStatement(trackQuery)) {
                    trackStatement.setString(1, artistId);
                    trackStatement.setString(2, track);
                    ResultSet trackResult = trackStatement.executeQuery();

                    if (!trackResult.next()) {
                        // Si la canción no está almacenada, insertarla
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

    // Consultar canciones almacenadas de un artista
    public static List<String> getTracksByArtistId(String artistId) throws SQLException {
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

    // Verificar si hay cambios entre los tracks actuales y los almacenados
    public static boolean hasTracksChanged(String artistId, List<String> newTracks) throws SQLException {
        List<String> existingTracks = getTracksByArtistId(artistId);
        return !existingTracks.equals(newTracks);
    }
}
