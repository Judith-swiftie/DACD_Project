package org.example;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import com.google.gson.Gson;

public class TicketmasterFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "prediction.TicketmasterEvents";  // Tópico para eventos de Ticketmaster

    public void fetchAndSendTicketmasterEvents() {
        // Simulamos un evento obtenido de la API de Ticketmaster (esto se debe hacer con una API real)
        Event event = new Event(System.currentTimeMillis(), "Ticketmaster", "Concierto B", "Barcelona", "Descripción del evento de Ticketmaster");

        sendEventToBroker(event); // Enviar el evento al broker
    }

    private void sendEventToBroker(Event event) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);

        try {
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(TOPIC_NAME);
            producer = session.createProducer(topic);

            Gson gson = new Gson();
            String jsonMessage = gson.toJson(event); // Serializa el evento a JSON
            TextMessage message = session.createTextMessage(jsonMessage);

            producer.send(message);
            System.out.println("Evento de Ticketmaster enviado: " + jsonMessage);
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (producer != null) {
                    producer.close();
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
}
