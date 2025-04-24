package org.example;

import jakarta.jms.JMSException;
import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyAuth;
import org.example.control.provider.SpotifyClient;
import org.example.control.store.SqliteMusicStore;

import java.util.List;
public class Main {
    public static void main(String[] args) throws JMSException {
        try {
            SpotifyFeeder feeder = new SpotifyFeeder();
            feeder.sendSpotifyEvents();

            SqliteMusicStore store = new SqliteMusicStore(System.getenv("DB_URL"));
            store.createTables();

            String token = SpotifyAuth.getAccessToken();

            SpotifyClient client = new SpotifyClient(token);
            SpotifyArtistService artistService = new SpotifyArtistService(client);

            String artistName = "Dua Lipa";
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
