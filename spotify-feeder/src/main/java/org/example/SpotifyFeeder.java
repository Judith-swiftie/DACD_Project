package org.example;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.control.store.MusicStore;
import org.example.control.store.SqliteMusicStore;

import javax.jms.*;
import java.sql.*;
import java.sql.Connection;
import java.util.List;

public class SpotifyFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "prediction.SpotifyEvents";
    private static final String DB_URL = System.getenv("DB_URL");

    public void sendStoredSpotifyEvents() {
        MusicStore store = new SqliteMusicStore();

        try (Connection sqlConnection = DriverManager.getConnection(DB_URL);
             Statement stmt = sqlConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM artists")) {

            while (rs.next()) {
                String artistId = rs.getString("id");
                String artistName = rs.getString("name");

                List<String> tracks = store.getTracksByArtistId(artistId);
                if (tracks.isEmpty()) continue;

                String description = "Tracks: " + String.join(", ", tracks);

                Event event = new Event(
                        System.currentTimeMillis(),
                        "Spotify",
                        artistName,
                        "España",
                        description
                );

                sendEventToBroker(event);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error consultando la base de datos: " + e.getMessage());
        }
    }

    private void sendEventToBroker(Event event) {
        javax.jms.Connection jmsConnection = null;
        Session session = null;
        MessageProducer producer = null;

        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        try {
            jmsConnection = factory.createConnection();
            jmsConnection.start();
            session = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            producer = session.createProducer(topic);

            Gson gson = new Gson();
            String jsonMessage = gson.toJson(event);
            TextMessage message = session.createTextMessage(jsonMessage);

            producer.send(message);
            System.out.println("✅ Evento enviado: " + jsonMessage);

        } catch (JMSException e) {
            System.err.println("❌ Error enviando al broker: " + e.getMessage());
        } finally {
            try {
                if (producer != null) producer.close();
                if (session != null) session.close();
                if (jmsConnection != null) jmsConnection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
