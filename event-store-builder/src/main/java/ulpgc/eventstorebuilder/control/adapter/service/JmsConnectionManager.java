package ulpgc.eventstorebuilder.control.adapter.service;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsConnectionManager {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private final ConnectionFactory factory;

    public JmsConnectionManager() {
        this.factory = new ActiveMQConnectionFactory(BROKER_URL);
    }

    public Connection createConnection(String clientId) throws JMSException {
        Connection connection = factory.createConnection();
        connection.setClientID(clientId);
        connection.start();
        return connection;
    }
}
