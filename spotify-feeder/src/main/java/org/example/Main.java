package org.example;

import org.example.control.provider.SpotifyArtistService;
import org.example.control.provider.SpotifyAuth;
import org.example.control.provider.SpotifyClient;
import org.example.control.store.SqliteMusicStore;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Crear la instancia de la base de datos y las tablas
            SqliteMusicStore store = new SqliteMusicStore();
            store.createTables();

            // Obtener el token de acceso para la API de Spotify
            String token = SpotifyAuth.getAccessToken();
            SpotifyClient client = new SpotifyClient(token);
            SpotifyArtistService artistService = new SpotifyArtistService(client);

            // Definir el nombre del artista y el código del país
            String artistName = "Lady Gaga";
            String countryCode = "ES";

            // Buscar el artista por nombre
            var artistData = artistService.findArtistByName(artistName);
            if (artistData != null) {
                String artistId = artistData.getString("id");

                // Obtener las canciones más populares del artista para el país indicado
                List<String> tracks = artistService.getTopTracksByCountry(artistId, countryCode);

                // Guardar los datos del artista y sus canciones en la base de datos
                store.saveArtistAndTracks(artistId, artistName, tracks);

                System.out.println("🎶 Playlist actualizada con las canciones de: " + artistName);
            } else {
                System.out.println("❌ No se encontró el artista.");
            }

        } catch (Exception e) {
            // Capturar errores y mostrar el mensaje de excepción
            System.err.println("🚨 Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
