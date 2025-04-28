package org.example;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.sql.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class TicketmasterFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "events";
    private static final String DB_URL = System.getenv("DB_URL");

    public void sendTicketmasterEvents() {
        try (Connection sqlConnection = DriverManager.getConnection(DB_URL);
             Statement stmt = sqlConnection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, date, time, venue, city, country, artists, priceInfo FROM events")) {

            while (rs.next()) {
                String name = rs.getString("name");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String venue = rs.getString("venue");
                String city = rs.getString("city");
                String country = rs.getString("country");
                String artists = rs.getString("artists");
                String priceInfo = rs.getString("priceInfo");

                String description = String.format("Artistas: %s | Lugar: %s, %s (%s) | Precio: %s", artists, venue, city, country, priceInfo);
                sendTicketmasterEventToBroker(name, description);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error consultando la base de datos: " + e.getMessage());
        }
    }

    private void sendTicketmasterEventToBroker(String name, String description) {
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

            long timestamp = System.currentTimeMillis();
            String sourceSystem = "TicketmasterFeeder";

            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("description", description);

            Event event = new Event(timestamp, sourceSystem, data);

            Gson gson = new Gson();
            String jsonMessage = gson.toJson(event);
            TextMessage message = session.createTextMessage(jsonMessage);

            producer.send(message);
            System.out.println("✅ Evento enviado a Ticketmaster: " + jsonMessage);

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
