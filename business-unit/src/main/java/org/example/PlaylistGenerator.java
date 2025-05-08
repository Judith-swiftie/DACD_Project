package org.example;

import org.example.model.Artist;
import org.example.model.Event;

import java.util.List;

public class PlaylistGenerator {

    private final SpotifyAPIClient spotifyAPIClient;

    public PlaylistGenerator() {
        this.spotifyAPIClient = new SpotifyAPIClient();
    }

    public void generatePlaylistForEvent(Event event) {
        List<Artist> artists = event.getArtists();
        for (Artist artist : artists) {
            // Conectar con la API de Spotify para crear una playlist para este artista
            spotifyAPIClient.createPlaylistForArtist(artist);
        }
    }

    public void generatePlaylistsFromEvents(List<Event> events) {
        for (Event event : events) {
            generatePlaylistForEvent(event);
        }
    }
}

