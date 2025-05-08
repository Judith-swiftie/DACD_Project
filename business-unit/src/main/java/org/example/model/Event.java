package org.example.model;

import java.util.List;

public class Event {
    private final String id;
    private final String name;
    private final List<Artist> artists;

    public Event(String id, String name, List<Artist> artists) {
        this.id = id;
        this.name = name;
        this.artists = artists;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Artist> getArtists() {
        return artists;
    }
}

