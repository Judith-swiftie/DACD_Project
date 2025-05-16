package ulpgc.playlistsforevents.control.store;

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
