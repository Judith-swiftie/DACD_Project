package ulpgc.spotifyfeeder;

import ulpgc.spotifyfeeder.control.adapter.Controller;
import ulpgc.spotifyfeeder.control.adapter.provider.SpotifyArtistService;
import ulpgc.spotifyfeeder.control.adapter.provider.SpotifyAuth;
import ulpgc.spotifyfeeder.control.adapter.provider.SpotifyClient;
import ulpgc.spotifyfeeder.control.adapter.store.ActiveMQMusicStore;
import ulpgc.spotifyfeeder.control.port.MusicStore;
import java.net.http.HttpClient;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String ARTIST_FILE_PATH = "artists.txt";
    private static final String BROKER_URL = "tcp://localhost:61616";

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        try {
            String token = obtainAccessToken();
            HttpClient client = HttpClient.newHttpClient();
            SpotifyClient spotifyClient = new SpotifyClient(client, token);
            SpotifyArtistService spotifyService = new SpotifyArtistService(spotifyClient);
            MusicStore musicStore = new ActiveMQMusicStore(BROKER_URL);
            List<String> artistNames = readArtistFile();
            Controller controller = new Controller(musicStore, spotifyService, spotifyService, artistNames);
            controller.fetchAndSendEvents();

        } catch (Exception e) {
            handleError(e);
        }
    }

    private String obtainAccessToken() throws Exception {
        String token = SpotifyAuth.getAccessToken();
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("No se pudo obtener un token de acceso de Spotify.");
        }
        return token;
    }

    private List<String> readArtistFile() {
        List<String> artists = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Main.ARTIST_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    artists.add(line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo archivo de artistas: " + e.getMessage());
        }
        return artists;
    }

    private void handleError(Exception e) {
        System.err.println("--- Error en la ejecución de la aplicación ---");
        System.err.println("Mensaje: " + e.getMessage());
        e.printStackTrace();
    }
}
