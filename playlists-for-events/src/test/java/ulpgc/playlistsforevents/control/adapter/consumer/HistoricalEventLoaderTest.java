package ulpgc.playlistsforevents.control.adapter.consumer;

import org.junit.jupiter.api.*;
import ulpgc.playlistsforevents.control.port.EventParser;
import ulpgc.playlistsforevents.model.Event;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoricalEventLoaderTest {
    private Path tempDir;
    private EventParser parserMock;
    private HistoricalEventLoader loader;

    @BeforeEach
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("eventsDir");
        parserMock = mock(EventParser.class);
        loader = new HistoricalEventLoader(tempDir.toString(), parserMock);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> p.toFile().delete());
    }

    @Test
    public void loadEvents_readsAndParsesUniqueEvents() throws IOException {
        Path file1 = Files.createFile(tempDir.resolve("file1.events"));
        Path file2 = Files.createFile(tempDir.resolve("file2.events"));
        List<String> linesFile1 = List.of(
                "{\"name\":\"Event1\",\"artists\":[]}",
                "{\"name\":\"Event2\",\"artists\":[]}"
        );
        List<String> linesFile2 = List.of(
                "{\"name\":\"Event2\",\"artists\":[]}",
                "{\"name\":\"Event3\",\"artists\":[]}"
        );
        Files.write(file1, linesFile1);
        Files.write(file2, linesFile2);
        Event event1 = new Event("Event1", List.of());
        Event event2 = new Event("Event2", List.of());
        Event event3 = new Event("Event3", List.of());
        when(parserMock.parse("{\"name\":\"Event1\",\"artists\":[]}")).thenReturn(Optional.of(event1));
        when(parserMock.parse("{\"name\":\"Event2\",\"artists\":[]}")).thenReturn(Optional.of(event2));
        when(parserMock.parse("{\"name\":\"Event3\",\"artists\":[]}")).thenReturn(Optional.of(event3));
        List<Event> loadedEvents = loader.loadEvents();
        assertEquals(3, loadedEvents.size());
        assertTrue(loadedEvents.containsAll(List.of(event1, event2, event3)));
    }

    @Test
    public void loadEvents_skipsEventsWithNullName() throws IOException {
        Path file = Files.createFile(tempDir.resolve("file.events"));
        List<String> lines = List.of(
                "{\"name\":null,\"artists\":[]}",
                "{\"name\":\"EventA\",\"artists\":[]}"
        );
        Files.write(file, lines);
        Event eventNull = new Event(null, List.of());
        Event eventA = new Event("EventA", List.of());
        when(parserMock.parse("{\"name\":null,\"artists\":[]}")).thenReturn(Optional.of(eventNull));
        when(parserMock.parse("{\"name\":\"EventA\",\"artists\":[]}")).thenReturn(Optional.of(eventA));
        List<Event> loadedEvents = loader.loadEvents();
        assertEquals(1, loadedEvents.size());
        assertTrue(loadedEvents.contains(eventA));
    }

    @Test
    public void loadEvents_returnsEmptyListOnIOException() {
        HistoricalEventLoader loaderBad = new HistoricalEventLoader("/invalid/path", parserMock);
        List<Event> events = loaderBad.loadEvents();
        assertTrue(events.isEmpty());
    }
}
