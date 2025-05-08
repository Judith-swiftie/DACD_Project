package org.example.datamart;

import org.example.model.Playlist;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDatamart {
    private final Map<String, Playlist> playlists = new HashMap<>();

    public void store(Playlist playlist) {
        playlists.put(playlist.getName(), playlist);
    }

    public Playlist get(String name) {
        return playlists.get(name);
    }
}

