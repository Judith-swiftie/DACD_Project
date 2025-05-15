package ulpgc.spotifyfeeder.control.store;

import ulpgc.spotifyfeeder.model.Artist;
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
        String createArtistsTable = """
            CREATE TABLE IF NOT EXISTS artists (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL
            )""";
        String createTracksTable = """
            CREATE TABLE IF NOT EXISTS tracks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                artist_id TEXT NOT NULL,
                track_name TEXT NOT NULL,
                UNIQUE(artist_id, track_name),
                FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
            )""";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.execute(createArtistsTable);
            statement.execute(createTracksTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT id, name FROM artists";
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String artistId = resultSet.getString("id");
                String artistName = resultSet.getString("name");
                artists.add(new Artist(artistId, artistName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    @Override
    public void store(String artistId, String artistName, List<String> tracks) {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(false);
            insertArtistIfNotExists(connection, artistId, artistName);
            insertTracksIfNotExists(connection, artistId, tracks);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
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
        return !new HashSet<>(existingTracks).equals(new HashSet<>(newTracks));
    }

    private void insertArtistIfNotExists(Connection connection, String artistId, String artistName) throws SQLException {
        String query = "SELECT id FROM artists WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, artistId);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {String insert = "INSERT INTO artists (id, name) VALUES (?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                    insertStatement.setString(1, artistId);
                    insertStatement.setString(2, artistName);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    private void insertTracksIfNotExists(Connection connection, String artistId, List<String> tracks) throws SQLException {
        String query = "SELECT track_name FROM tracks WHERE artist_id = ? AND track_name = ?";
        for (String track : tracks) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, artistId);
                statement.setString(2, track);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {String insert = "INSERT INTO tracks (artist_id, track_name) VALUES (?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                        insertStatement.setString(1, artistId);
                        insertStatement.setString(2, track);
                        insertStatement.executeUpdate();
                    }
                }
            }
        }
    }
}
