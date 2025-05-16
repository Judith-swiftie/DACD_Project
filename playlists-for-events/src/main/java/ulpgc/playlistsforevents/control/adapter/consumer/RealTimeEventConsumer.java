package ulpgc.playlistsforevents.control.adapter.consumer;

import ulpgc.playlistsforevents.control.port.EventProcessor;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class RealTimeEventConsumer {
    private final ConnectionFactory connectionFactory;
    private final String topicName;
    private final EventProcessor eventProcessor;

    public RealTimeEventConsumer(ConnectionFactory connectionFactory, String topicName, EventProcessor eventProcessor) {
        this.connectionFactory = connectionFactory;
        this.topicName = topicName;
        this.eventProcessor = eventProcessor;
    }

    public void startConsumingEvents() {
        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            Topic topic = createTopic(session);
            MessageConsumer consumer = createConsumer(session, topic);
            consumer.setMessageListener(this::handleMessage);
        } catch (JMSException e) {
            System.err.println("Error al conectar con el broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Connection createAndStartConnection() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        return connection;
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Topic createTopic(Session session) throws JMSException {
        return session.createTopic(topicName);
    }

    private MessageConsumer createConsumer(Session session, Topic topic) throws JMSException {
        return session.createConsumer(topic);
    }

    private void handleMessage(Message message) {
        if (message instanceof TextMessage textMessage) {
            try {
                String json = textMessage.getText();
                eventProcessor.process(json);
            } catch (JMSException e) {
                System.err.println("Error al procesar el mensaje JMS: " + e.getMessage());
            }
        } else {
            System.err.println("Mensaje recibido no es TextMessage");
        }
    }
}
