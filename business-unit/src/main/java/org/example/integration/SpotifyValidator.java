package org.example.integration;


import org.example.model.Artist;
import org.example.model.Event;
import org.example.spotify.SpotifyService;

public class SpotifyValidator {
    private final SpotifyService spotifyService;

    public SpotifyValidator(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public boolean allArtistsExistOnSpotify(Event event) {
        for (Artist artist : event.getArtists()) {
            if (!spotifyService.exists(artist.getName())) {
                return false;
            }
        }
        return true;
    }
}

