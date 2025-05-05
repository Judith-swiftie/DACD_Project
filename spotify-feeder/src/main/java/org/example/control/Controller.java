package org.example.control;

import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyClient;
import org.example.control.store.ActiveMQMusicStore;
import java.util.List;
import org.json.JSONObject;

public class Controller {

    private static final String BROKER_URL = "tcp://localhost:61616";  // URL del broker ActiveMQ
    private final ActiveMQMusicStore musicStore;
    private final SpotifyArtistService musicProvider;

    public Controller(String token) {
        SpotifyClient spotifyClient = new SpotifyClient(token);
        this.musicProvider = new SpotifyArtistService(spotifyClient);
        this.musicStore = new ActiveMQMusicStore(BROKER_URL, musicProvider);
    }

    public void fetchAndSendEvents() {
        String artistName = "Taylor Swift";

        try {
            JSONObject artist = musicProvider.findArtistByName(artistName);
            if (artist != null) {
                List<String> tracks = musicStore.getTracksByArtistId(artist.getString("id"));
                if (!tracks.isEmpty()) {
                    musicStore.store(artist.getString("id"), artist.getString("name"), tracks);
                } else {
                    System.out.println("⚠️ No se encontraron canciones populares para el artista: " + artistName);
                }
            } else {
                System.out.println("⚠️ No se encontró el artista: " + artistName);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener datos de Spotify o al enviar eventos al broker: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
