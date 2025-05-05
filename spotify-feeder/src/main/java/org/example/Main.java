package org.example;

import org.example.control.Controller;
import org.example.control.provider.SpotifyAuth;

public class Main {
    public static void main(String[] args) {
        try {
            String token = SpotifyAuth.getAccessToken();

            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("No se pudo obtener un token de acceso de Spotify.");
            }

            Controller feeder = new Controller(token);
            feeder.fetchAndSendEvents();

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
