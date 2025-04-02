import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyClient;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpotifyArtistServiceTest {

    // Simulamos el cliente de Spotify con una subclase
    private static class FakeSpotifyClient extends SpotifyClient {
        public FakeSpotifyClient() {
            super(null);
        }

        @Override
        public String sendGetRequest(String url) {
            // Simulamos una respuesta JSON de la API de Spotify
            return "{ \"artists\": { \"items\": [ { \"id\": \"06HL4z0CvFAxyc27GXpf02\", \"name\": \"Taylor Swift\" } ] } }";
        }
    }

    @Test
    void testGetArtistData() throws Exception {
        // Usamos el cliente simulado
        SpotifyClient client = new FakeSpotifyClient();
        SpotifyArtistService artistService = new SpotifyArtistService(client);

        // Llamamos al servicio con el cliente simulado
        JSONObject artistData = artistService.findArtistByName("Taylor Swift");

        // Verificamos los datos del artista
        assertNotNull(artistData, "Los datos del artista no pueden ser nulos.");
        assertEquals("06HL4z0CvFAxyc27GXpf02", artistData.getString("id"), "El id del artista no es correcto.");
    }
}
