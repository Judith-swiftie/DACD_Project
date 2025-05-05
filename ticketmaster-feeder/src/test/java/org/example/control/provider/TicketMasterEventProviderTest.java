package org.example.control.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Event;
import org.example.model.Artist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.net.http.*;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TicketMasterEventProviderTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @InjectMocks
    private TicketMasterEventProvider ticketMasterEventProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchMusicEvents_SuccessfulResponse() throws Exception {
        String responseBody = "{ \"_embedded\": { \"events\": [ { \"name\": \"Concert 1\", \"dates\": { \"start\": { \"localDate\": \"2025-05-10\", \"localTime\": \"20:00\" } }, \"_embedded\": { \"venues\": [ { \"name\": \"Stadium 1\", \"city\": { \"name\": \"Madrid\" }, \"country\": { \"name\": \"Spain\" } } ] }, \"classifications\": [ { \"segment\": { \"name\": \"Music\" } } ], \"_embedded\": { \"attractions\": [ { \"name\": \"Artist 1\" } ] }, \"priceRanges\": [ { \"min\": 20, \"max\": 50, \"currency\": \"EUR\" } ] } ] } }";

        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(responseBody);

        List<Event> events = ticketMasterEventProvider.fetchMusicEvents();

        assertNotNull(events);
        assertEquals(1, events.size());

        Event event = events.get(0);
        assertEquals("Concert 1", event.getName());
        assertEquals("2025-05-10", event.getDate());
        assertEquals("20:00", event.getTime());
        assertEquals("Stadium 1", event.getVenue());
        assertEquals("Madrid", event.getCity());
        assertEquals("Spain", event.getCountry());
        assertEquals("20.0 - 50.0 EUR", event.getPriceInfo());

        List<Artist> artists = event.getArtists();
        assertNotNull(artists);
        assertEquals(1, artists.size());
        assertEquals("Artist 1", artists.get(0).getName());
    }

    @Test
    public void testFetchMusicEvents_ApiError() throws Exception {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(404);

        List<Event> events = ticketMasterEventProvider.fetchMusicEvents();

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testParseEvent() throws JsonProcessingException {
        String eventJson = "{ \"name\": \"Concert 1\", \"dates\": { \"start\": { \"localDate\": \"2025-05-10\", \"localTime\": \"20:00\" } }, \"_embedded\": { \"venues\": [ { \"name\": \"Stadium 1\", \"city\": { \"name\": \"Madrid\" }, \"country\": { \"name\": \"Spain\" } } ] }, \"classifications\": [ { \"segment\": { \"name\": \"Music\" } } ], \"_embedded\": { \"attractions\": [ { \"name\": \"Artist 1\" } ] }, \"priceRanges\": [ { \"min\": 20, \"max\": 50, \"currency\": \"EUR\" } ] }";
        JsonNode eventNode = new ObjectMapper().readTree(eventJson);

        Event event = ticketMasterEventProvider.parseEvent(eventNode);

        assertNotNull(event);
        assertEquals("Concert 1", event.getName());
        assertEquals("2025-05-10", event.getDate());
        assertEquals("20:00", event.getTime());
        assertEquals("Stadium 1", event.getVenue());
        assertEquals("Madrid", event.getCity());
        assertEquals("Spain", event.getCountry());
        assertEquals("20.0 - 50.0 EUR", event.getPriceInfo());

        List<Artist> artists = event.getArtists();
        assertNotNull(artists);
        assertEquals(1, artists.size());
        assertEquals("Artist 1", artists.get(0).getName());
    }
}
