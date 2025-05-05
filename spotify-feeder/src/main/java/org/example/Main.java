package org.example;

import jakarta.jms.JMSException;
import org.example.control.Controller;
import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyAuth;
import org.example.control.provider.SpotifyClient;
import org.example.control.store.SqliteMusicStore;

import java.util.List;

public class Main {
    public static void main(String[] args) throws JMSException {
        try {
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl == null || dbUrl.isEmpty()) {
                throw new IllegalArgumentException("La URL de la base de datos no está configurada.");
            }

            Controller feeder = new Controller(dbUrl);
            feeder.sendSpotifyEvents();

            SqliteMusicStore store = new SqliteMusicStore(dbUrl);
            store.createTables();

            String token = SpotifyAuth.getAccessToken();
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("No se pudo obtener un token de acceso de Spotify.");
            }

            SpotifyClient client = new SpotifyClient(token);
            SpotifyArtistService artistService = new SpotifyArtistService(client);

            String artistName = "Dua Lipa";
            String countryCode = "ES";

            var artistData = artistService.findArtistByName(artistName);
            if (artistData != null) {
                String artistId = artistData.getString("id");

                List<String> tracks = artistService.getTopTracksByCountry(artistId, countryCode);

                store.store(artistId, artistName, tracks);

                System.out.println("🎶 Playlist actualizada con las canciones de: " + artistName);
            } else {
                System.out.println("❌ No se encontró el artista.");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("🚨 Error de configuración: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("🚨 Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
