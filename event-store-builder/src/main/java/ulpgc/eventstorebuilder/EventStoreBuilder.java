package ulpgc.eventstorebuilder;

import jakarta.jms.*;

public class EventStoreBuilder {
    private final String topicName;
    private final String clientId;
    private final JmsConnectionManager connectionManager;
    private final MessageProcessor messageProcessor;

    public EventStoreBuilder(String topicName, MessageProcessor messageProcessor) {
        this.topicName = topicName;
        this.clientId = "EventStoreClient_" + topicName;
        this.connectionManager = new JmsConnectionManager();
        this.messageProcessor = messageProcessor;
    }

    public void startEventStore() {
        try {
            Connection connection = connectionManager.createConnection(clientId);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, clientId);
            consumer.setMessageListener(messageProcessor::process);
            System.out.println("Esperando eventos en el topic: " + topicName);
        } catch (JMSException e) {
            System.err.println("Error de conexión con el broker: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
