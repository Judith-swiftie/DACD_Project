package org.example;

import com.google.gson.Gson;
import jakarta.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.control.store.EventFileStore;

public class EventStoreBuilder {

    private static final String BROKER_URL = "tcp://localhost:61616";

    private final String topicName;
    private final String clientId;
    private final ConnectionFactory factory;
    private final EventFileStore eventFileStore;
    private final Gson gson;

    public EventStoreBuilder(String topicName) {
        this(topicName, (ConnectionFactory) new ActiveMQConnectionFactory(BROKER_URL));
    }

    public EventStoreBuilder(String topicName, ConnectionFactory factory) {
        this.topicName = topicName;
        this.clientId = "EventStoreClient_" + topicName;
        this.factory = factory;
        this.eventFileStore = new EventFileStore();
        this.gson = new Gson();
    }

    public void startEventStore() {
        try {
            Connection connection = factory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, clientId);

            System.out.println("🟢 Esperando eventos en el topic: " + topicName);

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        String json = ((TextMessage) message).getText();
                        Event event = gson.fromJson(json, Event.class);
                        eventFileStore.storeEvent(topicName, event);
                        System.out.println("✅ Evento almacenado de: " + event.getSource());
                    } catch (Exception e) {
                        System.err.println("❌ Error procesando mensaje: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        } catch (JMSException e) {
            System.err.println("❌ Error de conexión con el broker: " + e.getMessage());
        }
    }
}
