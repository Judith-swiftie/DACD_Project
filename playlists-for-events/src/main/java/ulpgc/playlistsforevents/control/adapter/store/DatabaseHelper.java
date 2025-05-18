package ulpgc.playlistsforevents.control.adapter.store;

import ulpgc.playlistsforevents.control.port.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseHelper {
    private final ConnectionProvider connectionProvider;

    public DatabaseHelper(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }
}
