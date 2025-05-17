package ulpgc.playlistsforevents.control.adapter.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulpgc.playlistsforevents.control.port.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DatabaseHelperTest {
    private ConnectionProvider connectionProvider;
    private DatabaseHelper databaseHelper;

    @BeforeEach
    void setUp() {
        connectionProvider = mock(ConnectionProvider.class);
        databaseHelper = new DatabaseHelper(connectionProvider);
    }

    @Test
    void testGetConnectionReturnsProvidedConnection() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        when(connectionProvider.getConnection()).thenReturn(mockConnection);
        Connection result = databaseHelper.getConnection();
        assertEquals(mockConnection, result);
        verify(connectionProvider, times(1)).getConnection();
    }

    @Test
    void testGetConnectionThrowsSQLException() throws SQLException {
        when(connectionProvider.getConnection()).thenThrow(new SQLException("DB error"));
        try {
            databaseHelper.getConnection();
        } catch (SQLException e) {
            assertEquals("DB error", e.getMessage());
        }
        verify(connectionProvider, times(1)).getConnection();
    }
}
