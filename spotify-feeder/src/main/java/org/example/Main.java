package org.example;

import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {
        try {
            // Crear las tablas en la base de datos si no existen
            SpotifyDatabaseService.createTables();

            // Configurar la ejecución periódica cada 1 hora (3600 segundos)
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        // Obtener el token de acceso
                        String token = SpotifyAuth.getAccessToken();
                        SpotifyClient client = new SpotifyClient(token);

                        // Crear el servicio de artistas
                        SpotifyArtistService artistService = new SpotifyArtistService(client);

                        // Definir el nombre del artista y el código de país
                        String artistName = "Taylor Swift";
                        String countryCode = "ES";

                        // Buscar y generar la playlist del artista
                        JSONObject artistData = artistService.getArtistData(artistName);

                        if (artistData != null) {
                            // Obtener el ID del artista desde la respuesta JSON
                            String artistId = artistData.getString("id");  // Cambié "artistId" por "id", que es el campo correcto

                            // Obtener las canciones más populares
                            List<String> tracks = artistService.getTopTracksByCountry(artistId, countryCode);

                            // Verificar si hay cambios en las canciones y almacenarlas si es necesario
                            if (SpotifyDatabaseService.hasTracksChanged(artistId, tracks)) {
                                SpotifyDatabaseService.saveArtistAndTracks(artistId, artistName, tracks);
                                System.out.println("\n🎶 Se ha actualizado la base de datos con nuevas canciones para el artista: " + artistName);
                            } else {
                                System.out.println("\n🔄 No hubo cambios en las canciones del artista: " + artistName);
                            }
                        } else {
                            System.out.println("❌ No se pudo generar la playlist para el artista: " + artistName);
                        }
                    } catch (Exception e) {
                        System.out.println("🚨 Error al ejecutar la actualización periódica: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }, 0, 3600000);  // Ejecuta inmediatamente y luego cada 1 hora (3600000 ms)

        } catch (Exception e) {
            System.out.println("🚨 Error al ejecutar la aplicación:");
            e.printStackTrace();
        }
    }
}
