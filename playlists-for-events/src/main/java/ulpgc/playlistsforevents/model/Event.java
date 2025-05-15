package ulpgc.playlistsforevents.model;

import com.google.gson.Gson;
import java.util.List;

public class Event {
    private String id;
    private String name;
    private List<Artist> artists;

    public String getId() {
        return id;
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
