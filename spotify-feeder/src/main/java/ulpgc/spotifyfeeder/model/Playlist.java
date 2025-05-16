package ulpgc.spotifyfeeder.model;

import java.util.List;

public class Playlist {
    private final String name;
    private final List<Track> tracks;

    public Playlist(String name, List<Track> tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "Playlist{name='" + name + "', tracks=" + tracks + "}";
    }
}
