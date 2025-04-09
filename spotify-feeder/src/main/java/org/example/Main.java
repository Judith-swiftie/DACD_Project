package org.example;

import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyAuth;
import org.example.control.provider.SpotifyClient;
import org.example.control.store.SqliteMusicStore;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            SpotifyFeeder feeder = new SpotifyFeeder();
            feeder.sendStoredSpotifyEvents();
            SqliteMusicStore store = new SqliteMusicStore();
            store.createTables();

            String token = SpotifyAuth.getAccessToken();
            SpotifyClient client = new SpotifyClient(token);
            SpotifyArtistService artistService = new SpotifyArtistService(client);

            String artistName = "Lady Gaga";
            String countryCode = "ES";

            var artistData = artistService.findArtistByName(artistName);
            if (artistData != null) {
                String artistId = artistData.getString("id");

                List<String> tracks = artistService.getTopTracksByCountry(artistId, countryCode);

                store.saveArtistAndTracks(artistId, artistName, tracks);

                System.out.println("🎶 Playlist actualizada con las canciones de: " + artistName);
            } else {
                System.out.println("❌ No se encontró el artista.");
            }

        } catch (Exception e) {
            System.err.println("🚨 Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
