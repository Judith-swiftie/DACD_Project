package ulpgc.spotifyfeeder.control.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifyAuthTest {

    @Test
    public void testGetAccessToken() {
        try {
            String token = SpotifyAuth.getAccessToken();
            assertNotNull(token);
            assertFalse(token.isEmpty());
        } catch (Exception e) {
            fail("Error al obtener el token: " + e.getMessage());
        }
    }
}
