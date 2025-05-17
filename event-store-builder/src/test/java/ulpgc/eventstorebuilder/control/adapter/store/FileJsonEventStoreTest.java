package ulpgc.eventstorebuilder.control.adapter.store;

import static org.mockito.Mockito.*;
import ulpgc.eventstorebuilder.control.port.FileWriter;
import ulpgc.eventstorebuilder.control.port.JsonEventPathBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileJsonEventStoreTest {
    private JsonEventPathBuilder pathBuilder;
    private FileWriter fileWriter;
    private FileJsonEventStore jsonEventStore;

    @BeforeEach
    void setup() {
        pathBuilder = mock(JsonEventPathBuilder.class);
        fileWriter = mock(FileWriter.class);
        jsonEventStore = new FileJsonEventStore("myTopic", pathBuilder, fileWriter);
    }

    @Test
    void saveJson_shouldBuildPathAndAppendFile() throws IOException {
        String json = "{\"key\":\"value\"}";
        Path mockPath = Paths.get("some", "path.events");
        when(pathBuilder.buildPath("myTopic", json)).thenReturn(mockPath);
        jsonEventStore.saveJson(json);
        verify(pathBuilder).buildPath("myTopic", json);
        verify(fileWriter).append(mockPath, json);
    }

    @Test
    void saveJson_shouldHandleIOExceptionGracefully() throws IOException {
        String json = "{\"key\":\"value\"}";
        Path mockPath = Paths.get("some", "path.events");
        when(pathBuilder.buildPath("myTopic", json)).thenReturn(mockPath);
        doThrow(new IOException("Disk full")).when(fileWriter).append(mockPath, json);
        jsonEventStore.saveJson(json);
        verify(pathBuilder).buildPath("myTopic", json);
        verify(fileWriter).append(mockPath, json);
    }
}
