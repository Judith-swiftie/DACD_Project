package ulpgc.ticketmasterfeeder.control.store;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import ulpgc.ticketmasterfeeder.model.Event;
import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.Instant;

public class ActiveMQEventStore implements EventStore {
    private final List<Event> storedEvents = new ArrayList<>();
    private final String sourceName = "TicketmasterFeeder";
    private final ConnectionFactory connectionFactory;
    private final Gson gson = new Gson();

    public ActiveMQEventStore(String url) {
        this.connectionFactory = new ActiveMQConnectionFactory(url);
    }

    @Override
    public void saveEvents(List<Event> events) {
        storedEvents.addAll(events);
        try (Connection connection = connectionFactory.createConnection()) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(getTopicForEvent());
            MessageProducer producer = session.createProducer(topic);
            connection.start();
            for (Event event : events) {
                String json = wrapEventAsJson(event);
                TextMessage message = session.createTextMessage(json);
                producer.send(message);
                System.out.println("Evento enviado a topic '" + topic.getTopicName() + "': " + event.getName());
            }
        } catch (JMSException e) {
            System.err.println("---Error al enviar eventos a ActiveMQ: " + e.getMessage());
        }
    }

    private String wrapEventAsJson(Event event) {
        Map<String, Object> map = Map.of(
                "ts", Instant.now().toString(),
                "ss", sourceName,
                "name", event.getName(),
                "date", event.getDate(),
                "time", event.getTime(),
                "venue", event.getVenue(),
                "city", event.getCity(),
                "country", event.getCountry(),
                "artists", event.getArtists(),
                "priceInfo", event.getPriceInfo()
        );
        return gson.toJson(map);
    }

    private String getTopicForEvent() {
        return "events";
    }

    @Override
    public List<Event> getAllEvents() {
        return new ArrayList<>(storedEvents);
    }

}
