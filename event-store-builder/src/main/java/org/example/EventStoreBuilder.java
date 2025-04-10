package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;
import java.util.UUID;

public class EventStoreBuilder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private final String topicName;

    public EventStoreBuilder(String topicName) {
        this.topicName = topicName;
    }

    public void startEventStore() {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        try {
            String clientID = "EventStoreClient-" + topicName + "-" + UUID.randomUUID().toString();
            connection = factory.createConnection();
            connection.setClientID(clientID);
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            consumer = session.createDurableSubscriber(topic, "EventStoreSubscription-" + topicName);

            System.out.println("Esperando mensajes en el topic: " + topicName);

            while (true) {
                Message message = consumer.receive(10000);  // Esperar hasta 10 segundos para un mensaje

                if (message == null) {
                    System.out.println("No se recibió ningún mensaje en este ciclo.");
                } else {
                    if (message instanceof TextMessage) {
                        String jsonMessage = ((TextMessage) message).getText();
                        System.out.println("Mensaje JSON recibido: " + jsonMessage);

                        Gson gson = new Gson();
                        Event event = gson.fromJson(jsonMessage, Event.class);
                        saveEvent(event);
                    }
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (consumer != null) consumer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveEvent(Event event) {
        if (event == null) {
            System.out.println("Evento inválido, no se puede almacenar.");
            return;
        }

        if (event.getTs() <= 0) {
            System.out.println("Timestamp inválido para el evento: " + event);
            return;
        }

        String eventName;
        if ("Ticketmaster".equals(event.getSs())) {
            eventName = event.getName();
        } else {
            eventName = event.getArtistName();
        }

        if (eventName == null || eventName.isEmpty()) {
            System.out.println("Nombre del artista/evento vacío o nulo: " + event);
            return;
        }

        System.out.println("Nombre del evento/artista: " + eventName);

        if (event.getDescription() == null || event.getDescription().isEmpty()) {
            System.out.println("Descripción vacía o nula para el evento: " + event);
            return;
        }

        String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date(event.getTs()));

        File directory = new File("eventstore/" + event.getSs() + "/" + event.getSs() + "/" + dateString);
        if (!directory.exists()) {
            boolean dirsCreated = directory.mkdirs();
            if (dirsCreated) {
                System.out.println("Directorios creados: " + directory.getPath());
            } else {
                System.out.println("No se pudieron crear los directorios: " + directory.getPath());
            }
        }

        File eventFile = new File(directory, dateString + ".events");
        try (FileWriter writer = new FileWriter(eventFile, true)) {
            Gson gson = new Gson();
            String jsonMessage = gson.toJson(event);
            writer.write(jsonMessage + "\n");
            System.out.println("Evento almacenado en " + event.getSs() + ": " + jsonMessage);
        } catch (IOException e) {
            System.err.println("Error al escribir el evento en el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
