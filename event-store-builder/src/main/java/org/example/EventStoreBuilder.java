package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;

public class EventStoreBuilder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "prediction.SpotifyEvents"; // Cambia esto según el topic que estás suscribiendo

    public void startEventStore() {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        try {
            connection = factory.createConnection();
            connection.setClientID("EventStoreClient");  // Para que la suscripción sea duradera
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            consumer = session.createDurableSubscriber(topic, "EventStoreSubscription");

            // Consumir los mensajes
            while (true) {
                Message message = consumer.receive();  // Bloquea hasta que se recibe un mensaje

                if (message instanceof TextMessage) {
                    String jsonMessage = ((TextMessage) message).getText();
                    Gson gson = new Gson();
                    Event event = gson.fromJson(jsonMessage, Event.class);

                    // Organizar y almacenar el evento
                    saveEvent(event);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (consumer != null) {
                    consumer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveEvent(Event event) {
        // Crear la estructura de directorios
        String topic = "prediction.SpotifyEvents"; // Usa el mismo tópico del mensaje
        String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date(event.getTs()));

        File directory = new File("eventstore/" + topic + "/" + event.getSs() + "/" + dateString);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Guardar el evento en el archivo correspondiente
        File eventFile = new File(directory, dateString + ".events");
        try (FileWriter writer = new FileWriter(eventFile, true)) {
            Gson gson = new Gson();
            String jsonMessage = gson.toJson(event);
            writer.write(jsonMessage + "\n");
            System.out.println("Evento almacenado: " + jsonMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EventStoreBuilder eventStoreBuilder = new EventStoreBuilder();
        eventStoreBuilder.startEventStore();  // Inicia la escucha y almacenamiento de eventos
    }
}
