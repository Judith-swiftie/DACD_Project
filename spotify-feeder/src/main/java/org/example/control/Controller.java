package org.example.control;

import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyClient;
import org.example.control.store.ActiveMQMusicStore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class Controller {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private final ActiveMQMusicStore musicStore;
    private final SpotifyArtistService musicProvider;

    public Controller(String token) {
        SpotifyClient spotifyClient = new SpotifyClient(token);
        this.musicProvider = new SpotifyArtistService(spotifyClient);
        this.musicStore = new ActiveMQMusicStore(BROKER_URL, musicProvider);
    }

    public void fetchAndSendEvents() {
        List<String> artistNames = readArtistFile("artists.txt");

        for (String artistName : artistNames) {
            try {
                JSONObject artist = musicProvider.findArtistByName(artistName);
                if (artist != null) {
                    List<String> tracks = musicStore.getTracksByArtistId(artist.getString("id"));
                    if (!tracks.isEmpty()) {
                        musicStore.store(artist.getString("id"), artist.getString("name"), tracks);
                    } else {
                        System.out.println("No se encontraron canciones populares para el artista: " + artistName);
                    }
                } else {
                    System.out.println("No se encontró el artista: " + artistName);
                }
            } catch (Exception e) {
                System.err.println("---Error al obtener datos de Spotify o al enviar eventos al broker: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private List<String> readArtistFile(String filename) {
        List<String> artists = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
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
}

