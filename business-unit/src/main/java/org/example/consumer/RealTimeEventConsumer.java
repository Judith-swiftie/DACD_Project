package org.example.consumer;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.PlaylistGenerator;
import org.example.model.Event;

public class RealTimeEventConsumer {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private final String topicName;
    private final PlaylistGenerator playlistGenerator;

    public RealTimeEventConsumer(String topicName) {
        this.topicName = topicName;
        this.playlistGenerator = new PlaylistGenerator();
    }

    public void startConsumingEvents() {
        try {
            ConnectionFactory factory = (ConnectionFactory) new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);

            consumer.setMessageListener(this::handleMessage);
            System.out.println("Consumiendo eventos en el topic: " + topicName);

        } catch (JMSException e) {
            System.err.println("Error al conectar con el broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) {
        if (message instanceof TextMessage textMessage) {
            try {
                String json = textMessage.getText();
                Event event = Event.fromJson(json);
                playlistGenerator.generatePlaylistForEvent(event);
            } catch (JMSException e) {
                System.err.println("Error al procesar el mensaje: " + e.getMessage());
            }
        }
    }
}

