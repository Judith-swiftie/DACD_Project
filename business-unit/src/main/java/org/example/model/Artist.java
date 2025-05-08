package org.example.model;

public class Artist {
    private final String name;
    private final String spotifyId;

    public Artist(String name, String spotifyId) {
        this.name = name;
        this.spotifyId = spotifyId;
    }

    public String getName() {
        return name;
    }

    public String getSpotifyId() {
        return spotifyId;
    }
}

