package org.example;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.google.gson.Gson;

import java.util.Map;

public class SpotifyFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC = "playlist";
    private static final String CLIENT_ID = "spotifyFeeder";

    private final ConnectionFactory factory;
    private final Gson gson;

    public SpotifyFeeder() {
        this.factory = (ConnectionFactory) new ActiveMQConnectionFactory(BROKER_URL);
        this.gson = new Gson();
    }

    public void sendSpotifyEvents() {
        try {
            Connection connection = factory.createConnection();
            connection.setClientID(CLIENT_ID);
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC);
            MessageProducer producer = session.createProducer(topic);

            Event event = new Event(System.currentTimeMillis(), "SpotifyFeeder", Map.of("artist", "Dua Lipa"));
            String jsonEvent = gson.toJson(event);

            TextMessage message = session.createTextMessage(jsonEvent);
            producer.send(message);

            System.out.println("📡 Evento enviado al topic [" + TOPIC + "]:");
            System.out.println("    ➤ " + jsonEvent);


            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            System.err.println("❌ Error en la conexión o envío de mensaje: " + e.getMessage());
        }
    }
}
