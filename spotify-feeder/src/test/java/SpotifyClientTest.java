import org.example.control.provider.SpotifyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpotifyClientTest {

    private SpotifyClient spotifyClient;
    private HttpClient httpClientMock;

    @BeforeEach
    void setUp() {
        // Creamos un mock del HttpClient
        httpClientMock = Mockito.mock(HttpClient.class);

        // Creamos un mock de la respuesta del HttpClient
        HttpResponse<String> responseMock = Mockito.mock(HttpResponse.class);
        try {
            when(responseMock.body()).thenReturn("Mock response");
            when(responseMock.statusCode()).thenReturn(200);
            when(responseMock.headers()).thenReturn(HttpHeaders.of(new java.util.HashMap<>(), (k, v) -> true));
            when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(responseMock);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inyectamos el HttpClient simulado en el constructor de SpotifyClient
        //spotifyClient = new SpotifyClient("test-access-token", httpClientMock);
    }

    @Test
    void testSendGetRequest() throws Exception {
        // Llamamos al método con el cliente HTTP simulado
        String response = spotifyClient.sendGetRequest("http://mock-url.com");

        // Verificamos que la respuesta es la simulada
        assertEquals("Mock response", response, "La respuesta debería ser la simulada.");
    }
}
