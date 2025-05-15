package ulpgc.spotifyfeeder.control;

import ulpgc.spotifyfeeder.control.provider.SpotifyArtistService;
import ulpgc.spotifyfeeder.control.store.ActiveMQMusicStore;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class Controller {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private final ActiveMQMusicStore musicStore;
    private final SpotifyArtistService musicProvider;
    private final String artistFilePath;

    public Controller(SpotifyArtistService musicProvider, String artistFilePath) {
        this.musicProvider = musicProvider;
        this.artistFilePath = artistFilePath;
        this.musicStore = new ActiveMQMusicStore(BROKER_URL, musicProvider);
    }

    public void fetchAndSendEvents() {
        List<String> artistNames = readArtistFile();
        for (String artistName : artistNames) {
            processArtist(artistName);
        }
    }

    private void processArtist(String artistName) {
        try {
            JSONObject artist = musicProvider.findArtistByName(artistName);
            if (artist == null) {
                System.out.println("No se encontró el artista: " + artistName);
                return;
            }
            List<String> tracks = musicStore.getTracksByArtistId(artist.getString("id"));
            if (tracks.isEmpty()) {
                System.out.println("No se encontraron canciones populares para el artista: " + artistName);
                return;
            }
            musicStore.store(artist.getString("id"), artist.getString("name"), tracks);
        } catch (Exception e) {
            System.err.println("---Error procesando artista '" + artistName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> readArtistFile() {
        List<String> artists = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(artistFilePath))) {
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

