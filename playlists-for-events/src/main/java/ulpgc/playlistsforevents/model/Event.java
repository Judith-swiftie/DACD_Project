package ulpgc.playlistsforevents.model;

import com.google.gson.Gson;
import java.util.List;

public class Event {
    private final String name;
    private final List<Artist> artists;

    public Event(String name, List<Artist> artists) {
        this.name = name;
        this.artists = artists;
    }

    public String getName() {
        return name;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static Event fromJson(String json) {
        return new Gson().fromJson(json, Event.class);
    }
}
