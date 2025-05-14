package ulpgc.spotify.control.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifyClientTest {

    @Test
    public void testSendGetRequest() throws Exception {
        String accessToken = SpotifyAuth.getAccessToken();
        SpotifyClient client = new SpotifyClient(accessToken);

        String response = client.sendGetRequest("https://api.spotify.com/v1/search?q=Lady%20Gaga&type=artist");
        assertNotNull(response);
        assertTrue(response.contains("artists"));
    }
}
