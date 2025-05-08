package org.example.datamart;

import org.example.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datamart {
    private static final String DB_URL = "jdbc:sqlite:businessunit.db";

    public Datamart() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS events (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    json TEXT NOT NULL
                );
            """);

        } catch (SQLException e) {
            throw new RuntimeException("Error inicializando Datamart SQLite", e);
        }
    }

    public void addEvent(Event event) {
        String sql = "INSERT OR IGNORE INTO events(id, name, json) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getId());
            ps.setString(2, event.getName());
            ps.setString(3, event.toJson());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error añadiendo evento al Datamart", e);
        }
    }

    public List<Event> getAllEvents() {
        String sql = "SELECT json FROM events";
        List<Event> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String json = rs.getString("json");
                list.add(Event.fromJson(json));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error leyendo eventos del Datamart", e);
        }
        return list;
    }

    public List<Event> searchByName(String keyword) {
        String sql = "SELECT json FROM events WHERE name LIKE ?";
        List<Event> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(Event.fromJson(rs.getString("json")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando eventos por nombre", e);
        }
        return list;
    }

    public List<Event> searchByArtist(String artistName) {
        String sql = "SELECT json FROM events";
        List<Event> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Event ev = Event.fromJson(rs.getString("json"));
                if (ev.getArtists().stream()
                        .anyMatch(a -> a.getName().equalsIgnoreCase(artistName))) {
                    list.add(ev);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando eventos por artista", e);
        }
        return list;
    }
}
