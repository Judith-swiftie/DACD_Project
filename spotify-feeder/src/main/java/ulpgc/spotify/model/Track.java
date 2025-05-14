package ulpgc.spotify.model;

public class Track {
    private String id;
    private String name;
    private Artist artist;

    public Track(String id, String name, Artist artist) {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Artist getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "Track{id='" + id + "', name='" + name + "', artist=" + artist.getName() + "}";
    }
}
