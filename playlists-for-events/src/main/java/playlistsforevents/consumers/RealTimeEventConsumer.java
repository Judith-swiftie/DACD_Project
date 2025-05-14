package playlistsforevents.consumers;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import playlistsforevents.Datamart;
import playlistsforevents.model.Event;

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
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
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
                datamart.addEvent(event);
                System.out.println("Evento en tiempo real guardado: " + event.getName());
            } catch (JMSException e) {
                System.err.println("Error al procesar el mensaje JMS: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error al convertir el mensaje a evento: " + e.getMessage());
            }
        }
    }
}
