package ulpgc.playlistsforevents.model;

public class Track {
    private final String title;
    private final String uri;

    public Track(String title, String uri) {
        this.title = title;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }
}

