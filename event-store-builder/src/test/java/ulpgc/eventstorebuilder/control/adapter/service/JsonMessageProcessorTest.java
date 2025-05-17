package ulpgc.eventstorebuilder.control.adapter.service;

import ulpgc.eventstorebuilder.control.port.JsonEventStore;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class JsonMessageProcessorTest {

    private JsonEventStore jsonEventStore;
    private JsonMessageProcessor processor;

    @BeforeEach
    void setUp() {
        jsonEventStore = mock(JsonEventStore.class);
        processor = new JsonMessageProcessor(jsonEventStore);
    }

    @Test
    void shouldProcessTextMessageCorrectly() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn("{\"event\": \"test\"}");

        processor.process(message);

        verify(jsonEventStore).saveJson("{\"event\": \"test\"}");
    }

    @Test
    void shouldHandleJMSExceptionGracefully() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenThrow(new JMSException("JMS error"));
        processor.process(message);
        verify(jsonEventStore, never()).saveJson(anyString());
    }

    @Test
    void shouldHandleNonTextMessageGracefully() {
        Message message = mock(Message.class);
        processor.process(message);
        verify(jsonEventStore, never()).saveJson(anyString());
    }

    @Test
    void shouldHandleUnexpectedExceptionGracefully() throws Exception {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn("{\"invalid\": \"json\"}");
        doThrow(new RuntimeException("Unexpected")).when(jsonEventStore).saveJson(anyString());
        processor.process(message);
        verify(jsonEventStore).saveJson("{\"invalid\": \"json\"}");
    }
}
