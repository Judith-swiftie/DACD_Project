package org.example.control.store;

import org.example.model.Artist;
import org.example.model.Event;
import org.junit.jupiter.api.Test;

import javax.jms.*;

import java.util.List;

import static org.mockito.Mockito.*;

class ActiveMQEventStoreTest {

    @Test
    void testSaveEvents() throws JMSException {
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        MessageProducer producer = mock(MessageProducer.class);
        Topic topic = mock(Topic.class);
        TextMessage textMessage = mock(TextMessage.class);

        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic(anyString())).thenReturn(topic);
        when(session.createProducer(topic)).thenReturn(producer);

        ActiveMQEventStore activeMQEventStore = mock(ActiveMQEventStore.class);
        activeMQEventStore.saveEvents(List.of(new Event("Concierto de Electrónica", "2025-09-10", "23:00", "Club", "Valencia", "España",
                List.of(new Artist("DJ A")), "10 - 30 EUR")));

        verify(producer, times(1)).send(textMessage);
    }
}
