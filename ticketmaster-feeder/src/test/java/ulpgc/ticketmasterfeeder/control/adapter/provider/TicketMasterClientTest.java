package ulpgc.ticketmasterfeeder.control.adapter.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketMasterClientTest {

    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;
    private TicketMasterClient client;

    @BeforeEach
    void setup() {
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        client = new TicketMasterClient(mockHttpClient);
    }

    @Test
    void fetchPageReturnsBodyOn200() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{ \"some\": \"json\" }");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        String result = client.fetchPage(1);
        assertEquals("{ \"some\": \"json\" }", result);
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(captor.capture(), any());
        String expectedUrlPart = "page=1";
        assertTrue(captor.getValue().uri().toString().contains(expectedUrlPart));
    }

    @Test
    void fetchPageReturnsNullOn429AndSleeps() throws Exception {
        when(mockResponse.statusCode()).thenReturn(429);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        long start = System.currentTimeMillis();
        String result = client.fetchPage(2);
        long duration = System.currentTimeMillis() - start;
        assertNull(result);
        assertTrue(duration >= 60000, "Should wait at least 60 seconds on 429 status");
    }

    @Test
    void fetchPageReturnsNullOnOtherErrors() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        String result = client.fetchPage(3);
        assertNull(result);
    }
}
