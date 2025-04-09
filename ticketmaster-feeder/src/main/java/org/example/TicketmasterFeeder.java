package org.example;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.control.provider.Event;
import org.example.control.store.EventStore;
import org.example.control.store.SqliteEventStore;

import javax.jms.*;
import java.util.List;

public class TicketmasterFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "prediction.TicketmasterEvents";

    public void sendStoredTicketmasterEvents() {
        EventStore store = new SqliteEventStore();
        List<Event> events = store.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No hay eventos guardados para enviar.");
            return;
        }

        for (Event event : events) {
            sendEventToBroker(event);
        }
    }

    private void sendEventToBroker(Event event) {
        Connection jmsConnection = null;
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
            System.err.println("❌ Error al enviar el evento al broker: " + e.getMessage());
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
