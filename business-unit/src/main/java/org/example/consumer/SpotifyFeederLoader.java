package org.example.consumer;

import org.example.datamart.Datamart;
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

        try {
            long fileCount = Files.walk(Paths.get(spotifyFeederPath))
                    .filter(path -> path.toString().endsWith(".events"))
                    .peek(file -> {
                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                try {
                                    JSONObject node = new JSONObject(line);
                                    String name = node.getString("artistName");

                                    if (!seenArtists.contains(name)) {
                                        seenArtists.add(name);
                                        List<String> tracks = new ArrayList<>();
                                        JSONArray trackArray = node.getJSONArray("tracks");
                                        for (int i = 0; i < trackArray.length(); i++) {
                                            tracks.add(trackArray.getString(i));
                                        }
                                        datamart.addArtistTracks(name, tracks);
                                    }

                                } catch (Exception e) {
                                    System.err.println("Error parseando línea de SpotifyFeeder: " + e.getMessage());
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Error leyendo archivo SpotifyFeeder: " + file + " - " + e.getMessage());
                        }
                    })
                    .count();

            System.out.println("Archivos .events de Spotify procesados: " + fileCount);
        } catch (IOException e) {
            System.err.println("Error accediendo a archivos de SpotifyFeeder: " + e.getMessage());
        }

        System.out.println("Artistas únicos cargados desde SpotifyFeeder.");
    }
}
