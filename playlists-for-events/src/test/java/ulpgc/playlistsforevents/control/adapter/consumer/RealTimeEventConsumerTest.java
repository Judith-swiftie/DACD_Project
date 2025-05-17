package ulpgc.playlistsforevents.control.adapter.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ulpgc.playlistsforevents.control.port.EventProcessor;
import javax.jms.*;
import static org.mockito.Mockito.*;

public class RealTimeEventConsumerTest {
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageConsumer consumer;
    private EventProcessor eventProcessor;
    private RealTimeEventConsumer realTimeEventConsumer;

    @BeforeEach
    public void setUp() throws JMSException {
        connectionFactory = mock(ConnectionFactory.class);
        connection = mock(Connection.class);
        session = mock(Session.class);
        topic = mock(Topic.class);
        consumer = mock(MessageConsumer.class);
        eventProcessor = mock(EventProcessor.class);
        when(connectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createTopic(anyString())).thenReturn(topic);
        when(session.createConsumer(topic)).thenReturn(consumer);
        realTimeEventConsumer = new RealTimeEventConsumer(connectionFactory, "music-topic", eventProcessor);
    }

    @Test
    public void testStartConsumingEvents_successfullySetsUpConsumer() throws JMSException {
        realTimeEventConsumer.startConsumingEvents();
        verify(connectionFactory).createConnection();
        verify(connection).start();
        verify(session).createTopic("music-topic");
        verify(session).createConsumer(topic);
        verify(consumer).setMessageListener(any(MessageListener.class));
    }

    @Test
    public void testHandleMessage_validTextMessage_processed() throws JMSException {
        realTimeEventConsumer.startConsumingEvents();

        ArgumentCaptor<MessageListener> captor = ArgumentCaptor.forClass(MessageListener.class);
        verify(consumer).setMessageListener(captor.capture());
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn("{\"name\":\"Event\",\"artists\":[]}");
        MessageListener listener = captor.getValue();
        listener.onMessage(textMessage);
        verify(eventProcessor).process("{\"name\":\"Event\",\"artists\":[]}");
    }

    @Test
    public void testHandleMessage_nonTextMessage_loggedAsError() throws JMSException {
        realTimeEventConsumer.startConsumingEvents();
        ArgumentCaptor<MessageListener> captor = ArgumentCaptor.forClass(MessageListener.class);
        verify(consumer).setMessageListener(captor.capture());
        Message genericMessage = mock(Message.class);
        MessageListener listener = captor.getValue();
        listener.onMessage(genericMessage);
        verify(eventProcessor, never()).process(any());
    }

    @Test
    public void testHandleMessage_jmsExceptionDuringTextExtraction_logged() throws JMSException {
        realTimeEventConsumer.startConsumingEvents();
        ArgumentCaptor<MessageListener> captor = ArgumentCaptor.forClass(MessageListener.class);
        verify(consumer).setMessageListener(captor.capture());
        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenThrow(new JMSException("Text extraction failed"));
        MessageListener listener = captor.getValue();
        listener.onMessage(textMessage);
        verify(eventProcessor, never()).process(any());
    }
}
