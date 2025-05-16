package ulpgc.playlistsforevents.control.store;

import ulpgc.playlistsforevents.model.Event;
import ulpgc.playlistsforevents.control.consumers.EventParser;
import ulpgc.playlistsforevents.control.TrackProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datamart implements TrackProvider{
    private final DatabaseHelper dbHelper;
    private final EventParser eventParser;

    public Datamart(ConnectionProvider connectionProvider, EventParser eventParser) {
        this.dbHelper = new DatabaseHelper(connectionProvider);
        this.eventParser = eventParser;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createEventsTable = """
            CREATE TABLE IF NOT EXISTS events (
                name TEXT PRIMARY KEY,
                json TEXT NOT NULL
            );
        """;

        String createArtistsTracksTable = """
            CREATE TABLE IF NOT EXISTS artists_tracks (
                artist_name TEXT NOT NULL,
                track_name TEXT NOT NULL,
                PRIMARY KEY (artist_name, track_name)
            );
        """;

        try (Connection conn = dbHelper.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createEventsTable);
            stmt.execute(createArtistsTracksTable);
        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando Datamart SQLite", e);
        }
    }

    public void addEvent(Event event) {
        String sql = "INSERT OR IGNORE INTO events(name, json) VALUES (?, ?)";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getName());
            ps.setString(2, eventParser.toJson(event));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error añadiendo evento al Datamart", e);
        }
    }

    public void addArtistTracks(String artistName, List<String> tracks) {
        String sql = "INSERT OR IGNORE INTO artists_tracks(artist_name, track_name) VALUES (?, ?)";
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (String track : tracks) {
                ps.setString(1, artistName);
                ps.setString(2, track);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error añadiendo artista y canciones al Datamart", e);
        }
    }

    @Override
    public List<String> getTracksByArtist(String artistName) {
        String sql = "SELECT track_name FROM artists_tracks WHERE artist_name = ?";
        List<String> tracks = new ArrayList<>();
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, artistName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tracks.add(rs.getString("track_name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo canciones del artista", e);
        }
        return tracks;
    }

    public List<Event> searchByName(String keyword) {
        String sql = "SELECT json FROM events WHERE name LIKE ?";
        List<Event> results = new ArrayList<>();
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(eventParser.fromJson(rs.getString("json")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando eventos por nombre", e);
        }
        return results;
    }

    public List<Event> searchByArtist(String artistName) {
        String sql = "SELECT json FROM events";
        List<Event> result = new ArrayList<>();
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Event event = eventParser.fromJson(rs.getString("json"));
                if (event.getArtists().stream().anyMatch(a -> a.getName().equalsIgnoreCase(artistName))) {
                    result.add(event);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando eventos por artista", e);
        }
        return result;
    }

    public List<Event> getAllEvents() {
        String sql = "SELECT json FROM events";
        List<Event> events = new ArrayList<>();
        try (Connection conn = dbHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                events.add(eventParser.fromJson(rs.getString("json")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando todos los eventos", e);
        }
        return events;
    }
}
