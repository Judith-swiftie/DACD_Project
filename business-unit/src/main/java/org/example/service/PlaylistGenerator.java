package org.example.service;

import org.example.model.Artist;
import org.example.model.Playlist;
import org.example.model.Track;
import org.example.model.Event;
import org.example.spotify.SpotifyService;

import java.util.ArrayList;
import java.util.List;

public class PlaylistGenerator {
    private final SpotifyService spotifyService;

    public PlaylistGenerator(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public Playlist createPlaylist(Event event) {
        List<Track> tracks = new ArrayList<>();
        for (Artist artist : event.getArtists()) {
            List<Track> artistTracks = spotifyService.getTopTracks(artist.getName());
            tracks.addAll(artistTracks);
        }
        return new Playlist(event.getName(), tracks);
    }
}

