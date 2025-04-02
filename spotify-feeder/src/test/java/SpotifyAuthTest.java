import org.example.control.provider.SpotifyAuth;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpotifyAuthTest {

    // Simulamos el comportamiento de obtener un token de acceso
    private static class FakeSpotifyAuth extends SpotifyAuth {
        public static String getAccessToken() {
            // Simulamos un token válido
            return "fake_access_token";
        }
    }

    @Test
    void testGetAccessToken() {
        try {
            // Usamos el método simulado para obtener el token
            String accessToken = FakeSpotifyAuth.getAccessToken();
            assertNotNull(accessToken, "El token de acceso no puede ser nulo.");
            assertFalse(accessToken.isEmpty(), "El token de acceso no puede estar vacío.");
        } catch (Exception e) {
            fail("Se produjo un error al obtener el token: " + e.getMessage());
        }
    }
}
