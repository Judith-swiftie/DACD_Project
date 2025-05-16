package ulpgc.spotifyfeeder.control;

import ulpgc.spotifyfeeder.control.store.MusicStore;
import ulpgc.spotifyfeeder.control.provider.ArtistFinder;
import ulpgc.spotifyfeeder.control.provider.TrackProvider;
import java.util.List;
import org.json.JSONObject;

public class Controller {
    private final MusicStore musicStore;
    private final ArtistFinder artistFinder;
    private final TrackProvider trackProvider;
    private final List<String> artistNames;

    public Controller(MusicStore musicStore, ArtistFinder artistFinder, TrackProvider trackProvider, List<String> artistNames) {
        this.musicStore = musicStore;
        this.artistFinder = artistFinder;
        this.trackProvider = trackProvider;
        this.artistNames = artistNames;
    }

    public void fetchAndSendEvents() {
        for (String artistName : artistNames) {
            processArtist(artistName);
        }
    }

    private void processArtist(String artistName) {
        try {
            JSONObject artist = artistFinder.findArtistByName(artistName);
            if (artist == null) {
                System.out.println("No se encontró el artista: " + artistName);
                return;
            }

            List<String> tracks = trackProvider.getTopTracksByCountry(artist.getString("id"), "ES");
            if (tracks.isEmpty()) {
                System.out.println("No se encontraron canciones populares para el artista: " + artistName);
                return;
            }

            if (musicStore.hasTracksChanged(artist.getString("id"), tracks)) {
                musicStore.store(artist.getString("id"), artist.getString("name"), tracks);
            } else {
                System.out.println("No hay cambios en las canciones para: " + artistName);
            }

        } catch (Exception e) {
            System.err.println("--- Error procesando artista '" + artistName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
}
