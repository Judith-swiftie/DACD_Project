package org.example;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.sql.*;
import java.sql.Connection;

public class TicketmasterFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "playlist.TicketmasterEvents";
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

                // Aquí se crea la descripción del evento
                String description = String.format("Artistas: %s | Lugar: %s, %s (%s) | Precio: %s", artists, venue, city, country, priceInfo);

                // Llamar a la función para enviar el evento al broker, pasando los valores correctos
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
            String sourceSystem = "Ticketmaster";

            // Ahora se pasan todos los parámetros correctos al constructor de Event
            Event event = new Event(timestamp, sourceSystem, name, description);

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

    // Clase interna para representar los eventos
    class Event {
        private long ts;
        private String ss;
        private String name;
        private String description;

        public Event(long ts, String ss, String name, String description) {
            this.ts = ts;
            this.ss = ss;
            this.name = name;
            this.description = description;
        }

        public long getTs() {
            return ts;
        }

        public String getSs() {
            return ss;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
