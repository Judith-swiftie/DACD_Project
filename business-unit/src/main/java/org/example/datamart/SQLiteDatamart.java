package org.example.datamart;

import org.example.model.Playlist;
import org.example.model.Track;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatamart {
    private static final String DB_URL = "jdbc:sqlite:playlists.db";

    public SQLiteDatamart() {
        createTablesIfNotExist();
    }

    private void createTablesIfNotExist() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS playlists (
                    name TEXT PRIMARY KEY
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tracks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    playlist_name TEXT,
                    title TEXT,
                    uri TEXT,
                    FOREIGN KEY (playlist_name) REFERENCES playlists(name)
                );
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing SQLite database", e);
        }
    }

    public void store(Playlist playlist) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertPlaylist = conn.prepareStatement(
                    "INSERT OR IGNORE INTO playlists(name) VALUES (?)");
                 PreparedStatement insertTrack = conn.prepareStatement(
                         "INSERT INTO tracks(playlist_name, title, uri) VALUES (?, ?, ?)")) {

                insertPlaylist.setString(1, playlist.getName());
                insertPlaylist.executeUpdate();

                for (Track track : playlist.getTracks()) {
                    insertTrack.setString(1, playlist.getName());
                    insertTrack.setString(2, track.getTitle());
                    insertTrack.setString(3, track.getUri());
                    insertTrack.addBatch();
                }
                insertTrack.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error storing playlist in SQLite", e);
        }
    }

    public Playlist get(String name) {
        List<Track> tracks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT title, uri FROM tracks WHERE playlist_name = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String uri = rs.getString("uri");
                    tracks.add(new Track(title, uri));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving playlist from SQLite", e);
        }

        return tracks.isEmpty() ? null : new Playlist(name, tracks);
    }
}
