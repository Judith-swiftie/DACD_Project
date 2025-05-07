package org.example.control.store;

import org.example.model.Event;
import org.example.model.Artist;

import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteEventStore implements EventStore {

    private static final String DB_URL = System.getenv("DB_URL");

    public SqliteEventStore() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión exitosa a SQLite en: " + DB_URL);
            createTable();
        } catch (Exception e) {
            System.err.println("---Error al conectar a SQLite: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                date TEXT NOT NULL,
                time TEXT,
                venue TEXT NOT NULL,
                city TEXT,
                country TEXT,
                artists TEXT,
                priceInfo TEXT,
                UNIQUE(name, date, venue)
            )
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("---Error al crear tabla: " + e.getMessage());
        }
    }

    @Override
    public void saveEvents(List<Event> events) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            for (Event event : events) {
                if (!eventExists(conn, event)) {
                    insertEvent(conn, event);
                }
            }
        } catch (SQLException e) {
            System.err.println("---Error guardando eventos: " + e.getMessage());
        }
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String artistsRaw = rs.getString("artists");
                List<Artist> artistList = new ArrayList<>();
                if (artistsRaw != null && !artistsRaw.isEmpty()) {
                    for (String name : artistsRaw.split(",\\s*")) {
                        artistList.add(new Artist(name));
                    }
                }
                events.add(new Event(
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("venue"),
                        rs.getString("city"),
                        rs.getString("country"),
                        artistList,
                        rs.getString("priceInfo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("---Error al obtener eventos: " + e.getMessage());
        }

        return events;
    }

    @Override
    public Event findEventByName(String name) {
        String sql = "SELECT * FROM events WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String artistsRaw = rs.getString("artists");
                List<Artist> artistList = new ArrayList<>();
                if (artistsRaw != null && !artistsRaw.isEmpty()) {
                    for (String nameStr : artistsRaw.split(",\\s*")) {
                        artistList.add(new Artist(nameStr));
                    }
                }
                return new Event(
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("venue"),
                        rs.getString("city"),
                        rs.getString("country"),
                        artistList,
                        rs.getString("priceInfo")
                );
            }
        } catch (SQLException e) {
            System.err.println("---Error al buscar evento: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteEventByName(String name) {
        String sql = "DELETE FROM events WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("---Error al eliminar evento: " + e.getMessage());
        }
    }

    @Override
    public void updateEvent(Event event) {
        deleteEventByName(event.getName());
        saveEvents(List.of(event));
    }

    private boolean eventExists(Connection conn, Event event) throws SQLException {
        String sql = "SELECT COUNT(*) FROM events WHERE name = ? AND date = ? AND venue = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDate());
            pstmt.setString(3, event.getVenue());
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    private void insertEvent(Connection conn, Event event) throws SQLException {
        String sql = "INSERT INTO events (name, date, time, venue, city, country, artists, priceInfo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDate());
            pstmt.setString(3, event.getTime());
            pstmt.setString(4, event.getVenue());
            pstmt.setString(5, event.getCity());
            pstmt.setString(6, event.getCountry());

            String artistNames = event.getArtists().stream()
                    .map(Artist::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            pstmt.setString(7, artistNames);

            pstmt.setString(8, event.getPriceInfo());
            pstmt.executeUpdate();
        }
    }
}
