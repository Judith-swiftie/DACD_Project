package ulpgc.spotify.control.provider;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpotifyArtistServiceTest {

    @Test
    public void testGetArtistTopTracks() {
        try {
            String accessToken = SpotifyAuth.getAccessToken();
            SpotifyClient client = new SpotifyClient(accessToken);

            SpotifyArtistService service = new SpotifyArtistService(client);

            JSONObject artist = service.findArtistByName("Lady Gaga");
            assertNotNull(artist, "El artista no debería ser nulo");
            String artistId = artist.getString("id");

            List<String> tracks = service.getTopTracksByCountry(artistId, "US");

            assertNotNull(tracks, "La lista de canciones no debería ser nula");
            assertFalse(tracks.isEmpty(), "Debería haber canciones en la lista");

            boolean hasFamousSong = tracks.stream().anyMatch(name ->
                    name.toLowerCase().contains("shallow") || name.toLowerCase().contains("bad")
            );
            assertTrue(hasFamousSong, "Esperaba encontrar alguna canción conocida de Lady Gaga");

        } catch (Exception e) {
            fail("Ocurrió una excepción: " + e.getMessage());
        }
    }
}
