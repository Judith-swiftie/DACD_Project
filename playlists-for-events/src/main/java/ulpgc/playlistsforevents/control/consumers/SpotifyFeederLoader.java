package ulpgc.playlistsforevents.control.consumers;

import ulpgc.playlistsforevents.control.store.Datamart;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SpotifyFeederLoader {
    private final String spotifyFeederPath;
    private final Datamart datamart;

    public SpotifyFeederLoader(String spotifyFeederPath, Datamart datamart) {
        this.spotifyFeederPath = spotifyFeederPath;
        this.datamart = datamart;
    }

    public void loadArtistTracks() {
        Set<String> seenArtists = new HashSet<>();
        List<Path> eventFiles = getEventFiles();
        for (Path file : eventFiles) {processEventFile(file, seenArtists);}
    }

    private List<Path> getEventFiles() {
        try {
            return Files.walk(Paths.get(spotifyFeederPath))
                    .filter(path -> path.toString().endsWith(".events"))
                    .toList();
        } catch (IOException e) {
            System.err.println("Error accediendo a archivos de SpotifyFeeder: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private void processEventFile(Path file, Set<String> seenArtists) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line, seenArtists);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo SpotifyFeeder: " + file + " - " + e.getMessage());
        }
    }

    private void processLine(String line, Set<String> seenArtists) {
        try {
            JSONObject node = new JSONObject(line);
            String artistName = node.getString("artistName");
            if (seenArtists.add(artistName)) {
                List<String> tracks = extractTracks(node);
                datamart.addArtistTracks(artistName, tracks);
            }
        } catch (Exception e) {
            System.err.println("Error parseando línea de SpotifyFeeder: " + e.getMessage());
        }
    }

    private List<String> extractTracks(JSONObject node) {
        List<String> tracks = new ArrayList<>();
        JSONArray trackArray = node.getJSONArray("tracks");
        for (int i = 0; i < trackArray.length(); i++) {
            tracks.add(trackArray.getString(i));
        }
        return tracks;
    }
}