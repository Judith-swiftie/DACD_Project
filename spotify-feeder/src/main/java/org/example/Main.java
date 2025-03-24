package org.example;

import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        try {
            String token = SpotifyAuth.getAccessToken();
            SpotifyClient client = new SpotifyClient(token);
            System.out.println("🔑 Token de acceso: " + token);

            SpotifyArtistService artistService = new SpotifyArtistService(client);

            String artistName = "Taylor Swift";
            String countryCode = "ES";

            JSONObject playlist = artistService.createPlaylistFromArtist(artistName, countryCode);

            if (playlist != null) {
                System.out.println("\n🎵 Playlist generada para el artista: " + artistName);
                System.out.println("📍 País: " + countryCode);
                System.out.println("🎶 Canciones: " + playlist.getJSONArray("tracks"));
            } else {
                System.out.println("❌ No se pudo crear la playlist.");
            }
        } catch (Exception e) {
            System.out.println("🚨 Error al ejecutar la aplicación:");
            e.printStackTrace();
        }
    }
}