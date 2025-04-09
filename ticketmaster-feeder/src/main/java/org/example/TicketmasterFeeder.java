package org.example;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.control.store.EventStore;
import org.example.control.store.SqliteEventStore;

import javax.jms.*;
import java.util.List;
import java.util.ArrayList;

public class TicketmasterFeeder {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC_NAME = "concert.TicketmasterEvents";

    public void sendStoredTicketmasterEvents() {
        EventStore store = new SqliteEventStore();
        List<org.example.control.provider.Event> events = store.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No hay eventos guardados para enviar.");
            return;
        }

        List<TicketmasterFeeder.Event> mappedEvents = mapEvents(events);

        for (TicketmasterFeeder.Event event : mappedEvents) {
            sendEventToBroker(event);
        }
    }

    private List<TicketmasterFeeder.Event> mapEvents(List<org.example.control.provider.Event> events) {
        List<TicketmasterFeeder.Event> mappedEvents = new ArrayList<>();

        for (org.example.control.provider.Event event : events) {
            TicketmasterFeeder.Event mappedEvent = new TicketmasterFeeder.Event(
                    event.getName(),
                    event.getDate(),
                    event.getTime(),
                    event.getVenue(),
                    event.getCity(),
                    event.getCountry(),
                    event.getArtists(),
                    event.getPriceInfo()
            );
            mappedEvents.add(mappedEvent);
        }

        return mappedEvents;
    }

    // Método para enviar un evento al broker
    private void sendEventToBroker(TicketmasterFeeder.Event event) {
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
            System.out.println("✅ Evento enviado a Ticketmaster: " + jsonMessage);
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

    public static class Event {
        private String name;
        private String date;
        private String time;
        private String venue;
        private String city;
        private String country;
        private String artists;
        private String priceInfo;

        public Event(String name, String date, String time, String venue, String city, String country, String artists, String priceInfo) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.venue = venue;
            this.city = city;
            this.country = country;
            this.artists = artists;
            this.priceInfo = priceInfo;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getVenue() {
            return venue;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public String getArtists() {
            return artists;
        }

        public String getPriceInfo() {
            return priceInfo;
        }
    }
}
