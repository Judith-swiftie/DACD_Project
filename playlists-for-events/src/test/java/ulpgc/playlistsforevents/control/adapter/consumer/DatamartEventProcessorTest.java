package ulpgc.playlistsforevents.control.adapter.consumer;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulpgc.playlistsforevents.control.adapter.store.Datamart;

public class DatamartEventProcessorTest {
    private Datamart datamart;
    private DatamartEventProcessor processor;

    @BeforeEach
    public void setup() {
        datamart = mock(Datamart.class);
        processor = new DatamartEventProcessor(datamart);
    }

    @Test
    public void testProcess_callsAddEventWithParsedEvent() {
        String json = "{\"name\":\"Concert\"}";
        processor.process(json);
        verify(datamart).addEvent(argThat(event ->
                event.getName().equals("Concert")
        ));
    }
}
