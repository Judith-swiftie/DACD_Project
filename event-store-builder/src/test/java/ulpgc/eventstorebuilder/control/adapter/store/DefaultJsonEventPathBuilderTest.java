package ulpgc.eventstorebuilder.control.adapter.store;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DefaultJsonEventPathBuilderTest {

    @Test
    void buildPath_shouldReturnExpectedPath() {
        DefaultJsonEventPathBuilder builder = new DefaultJsonEventPathBuilder();
        String topicName = "testTopic";
        String timestamp = "2025-05-17T14:30:00Z";
        String ssValue = "session123";
        String json = String.format("{\"ss\":\"%s\",\"ts\":\"%s\"}", ssValue, timestamp);
        Path expectedPath = Path.of("eventstore", topicName, ssValue,
                ZonedDateTime.parse(timestamp)
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".events");
        Path actualPath = builder.buildPath(topicName, json);
        assertEquals(expectedPath, actualPath);
    }
}
