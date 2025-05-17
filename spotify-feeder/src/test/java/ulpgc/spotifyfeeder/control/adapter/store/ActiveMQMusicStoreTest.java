package ulpgc.spotifyfeeder.control.adapter.store;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActiveMQMusicStoreTest {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;
    private TextMessage message;
    private ActiveMQMusicStore musicStore;

    @BeforeEach
    void setUp() throws Exception {
        connectionFactory = mock(ActiveMQConnectionFactory.class);
        connection = mock(Connection.class);
        session = mock(Session.class);
        topic = mock(Topic.class);
        producer = mock(MessageProducer.class);
        message = mock(TextMessage.class);
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic(anyString())).thenReturn(topic);
        when(session.createProducer(topic)).thenReturn(producer);
        when(session.createTextMessage(anyString())).thenReturn(message);
        musicStore = new ActiveMQMusicStore("mock") {
            protected Connection createConnection() throws JMSException {
                return connectionFactory.createConnection();
            }

            protected Session createSession(Connection conn) throws JMSException {
                return session;
            }

            protected Topic createTopic(Session sess, String name) throws JMSException {
                return topic;
            }
        };
    }

    @Test
    void testHasTracksChangedReturnsTrueWhenTracksDiffer() {
        ActiveMQMusicStore musicStoreSpy = spy(new ActiveMQMusicStore("mock"));
        doReturn(List.of("Old Song")).when(musicStoreSpy).getTracksByArtistId("xyz");
        boolean result = musicStoreSpy.hasTracksChanged("xyz", List.of("New Song"));
        assertTrue(result);
    }

    @Test
    void testHasTracksChangedReturnsFalseWhenTracksAreEqual() {
        ActiveMQMusicStore musicStoreSpy = spy(new ActiveMQMusicStore("mock"));
        List<String> tracks = List.of("Same Song");
        doReturn(tracks).when(musicStoreSpy).getTracksByArtistId("xyz");
        boolean result = musicStoreSpy.hasTracksChanged("xyz", tracks);
        assertFalse(result);
    }
}
