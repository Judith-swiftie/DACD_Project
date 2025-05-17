package ulpgc.playlistsforevents.control.adapter.service;

import org.junit.jupiter.api.*;
import ulpgc.playlistsforevents.control.adapter.store.Datamart;
import ulpgc.playlistsforevents.model.Event;
import ulpgc.playlistsforevents.model.Artist;
import java.io.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventSelectorTest {

    private Datamart datamart;
    private EventSelector selector;
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setup() {
        datamart = mock(Datamart.class);
        selector = new EventSelector(datamart);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreSystemStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void selectEventFromUserInput_NoEventsAvailable_ReturnsEmpty() {
        when(datamart.getAllEvents()).thenReturn(List.of());
        Optional<Event> result = selector.selectEventFromUserInput();
        assertTrue(result.isEmpty());
        assertTrue(outContent.toString().contains("No hay eventos disponibles."));
    }

    @Test
    void selectEventFromUserInput_ValidSelection_ReturnsCorrectEvent() {
        Event e1 = new Event("Evento 1", List.of(new Artist("Artista 1")));
        Event e2 = new Event("Evento 2", List.of(new Artist("Artista 2")));
        when(datamart.getAllEvents()).thenReturn(List.of(e1, e2));
        String input = "2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Optional<Event> result = selector.selectEventFromUserInput();
        assertTrue(result.isPresent());
        assertEquals("Evento 2", result.get().getName());
        String output = outContent.toString();
        assertTrue(output.contains("Eventos disponibles:"));
        assertTrue(output.contains("1. Evento 1"));
        assertTrue(output.contains("2. Evento 2"));
        assertTrue(output.contains("Selecciona el número del evento deseado:"));
    }

    @Test
    void selectEventFromUserInput_InvalidSelection_ReturnsEmpty() {
        Event e1 = new Event("Evento 1", List.of(new Artist("Artista 1")));
        when(datamart.getAllEvents()).thenReturn(List.of(e1));
        String input = "5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Optional<Event> result = selector.selectEventFromUserInput();
        assertTrue(result.isEmpty());
        assertTrue(outContent.toString().contains("Selección inválida."));
    }

    @Test
    void selectEventFromUserInput_NonIntegerInput_ThenValidInput_ReturnsCorrectEvent() {
        Event e1 = new Event("Evento 1", List.of(new Artist("Artista 1")));
        when(datamart.getAllEvents()).thenReturn(List.of(e1));
        String input = "abc\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Optional<Event> result = selector.selectEventFromUserInput();
        assertTrue(result.isPresent());
        assertEquals("Evento 1", result.get().getName());
        String output = outContent.toString();
        assertTrue(output.contains("Entrada inválida. Introduce un número:"));
        assertTrue(output.contains("Selecciona el número del evento deseado:"));
    }
}
