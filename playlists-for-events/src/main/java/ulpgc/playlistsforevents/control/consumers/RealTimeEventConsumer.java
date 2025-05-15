package ulpgc.playlistsforevents.control.consumers;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import ulpgc.playlistsforevents.control.store.Datamart;
import ulpgc.playlistsforevents.model.Event;

public class RealTimeEventConsumer {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private final String topicName;
    private final Datamart datamart;

    public RealTimeEventConsumer(String topicName, Datamart datamart) {
        this.datamart = datamart;
        this.topicName = topicName;
    }

    public void startConsumingEvents() {
        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            Topic topic = createTopic(session);
            MessageConsumer consumer = createConsumer(session, topic);
            consumer.setMessageListener(this::handleMessage);
        } catch (JMSException e) {
            handleConnectionError(e);
        }
    }

    private Connection createAndStartConnection() throws JMSException {
        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = factory.createConnection();
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

    private void handleConnectionError(JMSException e) {
        System.err.println("Error al conectar con el broker: " + e.getMessage());
        e.printStackTrace();
    }

    private void handleMessage(Message message) {
        if (message instanceof TextMessage textMessage) {
            try {
                String json = textMessage.getText();
                Event event = Event.fromJson(json);
                datamart.addEvent(event);
            } catch (JMSException e) {
                System.err.println("Error al procesar el mensaje JMS: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error al convertir el mensaje a evento: " + e.getMessage());
            }
        }
    }
}
