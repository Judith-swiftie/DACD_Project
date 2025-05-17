package ulpgc.ticketmasterfeeder.control.adapter.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.jms.*;
import ulpgc.ticketmasterfeeder.model.Artist;
import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActiveMQEventStoreTest {
    private ConnectionFactory mockConnectionFactory;
    private Connection mockConnection;
    private Session mockSession;
    private Topic mockTopic;
    private MessageProducer mockProducer;
    private ActiveMQEventStore eventStore;

    @BeforeEach
    void setUp() throws JMSException {
        mockConnectionFactory = mock(ConnectionFactory.class);
        mockConnection = mock(Connection.class);
        mockSession = mock(Session.class);
        mockTopic = mock(Topic.class);
        mockProducer = mock(MessageProducer.class);
        when(mockConnectionFactory.createConnection()).thenReturn(mockConnection);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createTopic("events")).thenReturn(mockTopic);
        when(mockSession.createProducer(mockTopic)).thenReturn(mockProducer);
        when(mockTopic.getTopicName()).thenReturn("events");
        eventStore = new ActiveMQEventStore("mock-url") {
            protected ConnectionFactory createConnectionFactory(String url) {
                return mockConnectionFactory;
            }
        };
    }

    @Test
    void getAllEvents_returnsCopyOfStoredEvents() {
        Artist artist = new Artist("Another Artist");
        Event event = new Event(
                "Show",
                "2025-06-01",
                "19:00",
                "AnotherVenue",
                "AnotherCity",
                "AnotherCountry",
                List.of(artist),
                "15-30 EUR"
        );

        eventStore.saveEvents(List.of(event));
        List<Event> storedEvents = eventStore.getAllEvents();
        assertEquals(1, storedEvents.size());
        assertEquals(event.getName(), storedEvents.get(0).getName());
        storedEvents.add(event);
        assertEquals(1, eventStore.getAllEvents().size(), "Los eventos almacenados no deben ser modificables externamente");
    }
}
