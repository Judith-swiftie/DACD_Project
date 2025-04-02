package org.example.control.store;

import org.example.control.provider.Event;

import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteEventStore implements EventStore {
    private static final String DB_URL = System.getenv("DB_URL");

    public SqliteEventStore() {
        try {
            Class.forName("org.sqlite.JDBC");

            String dbPath = Paths.get("database.db").toAbsolutePath().toString();
            System.out.println("La base de datos se guarda en: " + dbPath);

            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión exitosa a la base de datos SQLite");

            createTable();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS events (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "time TEXT, " +
                    "venue TEXT NOT NULL, " +
                    "city TEXT, " +
                    "country TEXT, " +
                    "artists TEXT, " +
                    "priceInfo TEXT, " +
                    "UNIQUE(name, date, venue))";
            stmt.execute(sql);
            System.out.println("Tabla 'events' creada (si no existía).");
        } catch (SQLException e) {
            System.err.println("Error al crear la tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void saveEvents(List<Event> events) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTable();

            for (Event event : events) {
                if (!eventExists(conn, event)) {
                    insertEvent(conn, event);
                } else {
                    System.out.println("El evento ya existe: " + event.getName());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar los eventos: " + e.getMessage());
            e.printStackTrace();
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
                Event event = new Event(
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("venue"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("artists"),
                        rs.getString("priceInfo")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener los eventos: " + e.getMessage());
            e.printStackTrace();
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
                return new Event(
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("venue"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("artists"),
                        rs.getString("priceInfo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el evento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void deleteEventByName(String name) {
        String sql = "DELETE FROM events WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Eventos eliminados: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error al eliminar el evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateEvent(Event event) {
        String sql = "UPDATE events SET date = ?, time = ?, venue = ?, city = ?, country = ?, artists = ?, priceInfo = ? WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getDate());
            pstmt.setString(2, event.getTime());
            pstmt.setString(3, event.getVenue());
            pstmt.setString(4, event.getCity());
            pstmt.setString(5, event.getCountry());
            pstmt.setString(6, event.getArtists());
            pstmt.setString(7, event.getPriceInfo());
            pstmt.setString(8, event.getName());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Eventos actualizados: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error al actualizar el evento: " + e.getMessage());
            e.printStackTrace();
        }
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
            pstmt.setString(7, event.getArtists());
            pstmt.setString(8, event.getPriceInfo());
            pstmt.executeUpdate();
            System.out.println("Evento insertado: " + event.getName());
        }
    }
}
