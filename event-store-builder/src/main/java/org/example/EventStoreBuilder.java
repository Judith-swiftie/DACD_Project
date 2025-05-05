package org.example;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class EventStoreBuilder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private final String topicName;
    private final String clientId;
    private final ConnectionFactory factory;
    private final JsonEventStore jsonEventStore;

    public EventStoreBuilder(String topicName, JsonEventStore jsonEventStore) {
        this.topicName = topicName;
        this.clientId = "EventStoreClient_" + topicName;
        this.factory = new ActiveMQConnectionFactory(BROKER_URL);
        this.jsonEventStore = jsonEventStore;
    }

    public void startEventStore() {
        try {
            Connection connection = factory.createConnection();
            connection.setClientID(clientId);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, clientId);  // Suscripción duradera

            System.out.println("🟢 Esperando eventos en el topic: " + topicName);

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage textMessage) {
                    try {
                        String json = textMessage.getText();
                        System.out.println("📥 Mensaje recibido: " + json);
                        jsonEventStore.saveJson(json);
                        System.out.println("✅ Evento JSON almacenado.");
                    } catch (Exception e) {
                        System.err.println("❌ Error procesando el mensaje: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("⚠️ Mensaje recibido no es de tipo TextMessage.");
                }
            });

        } catch (JMSException e) {
            System.err.println("❌ Error de conexión con el broker: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
