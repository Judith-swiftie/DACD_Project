package ulpgc.spotifyfeeder;

import ulpgc.spotifyfeeder.control.Controller;
import ulpgc.spotifyfeeder.control.provider.SpotifyAuth;
import ulpgc.spotifyfeeder.control.provider.SpotifyClient;
import ulpgc.spotifyfeeder.control.provider.SpotifyArtistService;

public class Main {
    private static final String ARTIST_FILE_PATH = "artists.txt";

    public static void main(String[] args) {
        try {
            String token = SpotifyAuth.getAccessToken();
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("No se pudo obtener un token de acceso de Spotify.");
            }
            SpotifyClient spotifyClient = new SpotifyClient(token);
            SpotifyArtistService musicProvider = new SpotifyArtistService(spotifyClient);
            Controller controller = new Controller(musicProvider, ARTIST_FILE_PATH);
            controller.fetchAndSendEvents();
        } catch (Exception e) {
            System.err.println("---Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
