package ulpgc.eventstorebuilder.control.adapter.service;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class JmsConnectionManagerTest {

    @Test
    public void testCreateConnection_setsClientIdAndStartsConnection() throws JMSException {
        String clientId = "TestClient";
        ConnectionFactory factoryMock = mock(ActiveMQConnectionFactory.class);
        Connection connectionMock = mock(Connection.class);
        when(factoryMock.createConnection()).thenReturn(connectionMock);
        JmsConnectionManager manager = new JmsConnectionManager() {
            @Override
            public Connection createConnection(String clientId) throws JMSException {
                Connection conn = factoryMock.createConnection();
                conn.setClientID(clientId);
                conn.start();
                return conn;
            }
        };
        Connection connection = manager.createConnection(clientId);
        verify(connectionMock).setClientID(clientId);
        verify(connectionMock).start();
        assertEquals(connectionMock, connection);
    }
}
