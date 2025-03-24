package org.example;

import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:ticketmaster.db";

    public DatabaseManager() {
        try {
            // Registra el controlador SQLite
            Class.forName("org.sqlite.JDBC");

            // Obtiene la ruta absoluta del archivo de base de datos
            String dbPath = Paths.get("ticketmaster.db").toAbsolutePath().toString();
            System.out.println("La base de datos se guarda en: " + dbPath);

            // Intenta conectar a la base de datos
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión exitosa a la base de datos SQLite");

            // Crear la tabla si no existe
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

    public void saveEvents(List<Event> events) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Asegúrate de que la tabla 'events' esté presente antes de insertar los eventos
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
