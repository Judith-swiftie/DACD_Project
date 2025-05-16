package ulpgc.playlistsforevents.control.adapter.consumer;

import ulpgc.playlistsforevents.control.port.FeederLoader;
import ulpgc.playlistsforevents.control.adapter.store.Datamart;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpotifyFeederLoader implements FeederLoader {
    private final String spotifyFeederPath;
    private final Datamart datamart;
    private final FileReaderService fileReaderService;
    private final SpotifyJsonParser parser;

    public SpotifyFeederLoader(String spotifyFeederPath, Datamart datamart,
                               FileReaderService fileReaderService, SpotifyJsonParser parser) {
        this.spotifyFeederPath = spotifyFeederPath;
        this.datamart = datamart;
        this.fileReaderService = fileReaderService;
        this.parser = parser;
    }

    @Override
    public void load() {
        Set<String> seenArtists = new HashSet<>();
        List<Path> eventFiles;
        try {
            eventFiles = fileReaderService.listFiles(spotifyFeederPath, ".events");
        } catch (IOException e) {
            System.err.println("Error accediendo a archivos de SpotifyFeeder: " + e.getMessage());
            return;
        }

        for (Path file : eventFiles) {
            processEventFile(file, seenArtists);
        }
    }

    private void processEventFile(Path file, Set<String> seenArtists) {
        List<String> lines;
        try {
            lines = fileReaderService.readLines(file);
        } catch (IOException e) {
            System.err.println("Error leyendo archivo SpotifyFeeder: " + file + " - " + e.getMessage());
            return;
        }

        for (String line : lines) {
            processLine(line, seenArtists);
        }
    }

    private void processLine(String line, Set<String> seenArtists) {
        try {
            String artistName = parser.parseArtistName(line);
            if (seenArtists.add(artistName)) {
                List<String> tracks = parser.parseTracks(line);
                datamart.addArtistTracks(artistName, tracks);
            }
        } catch (Exception e) {
            System.err.println("Error parseando línea de SpotifyFeeder: " + e.getMessage());
        }
    }
}
