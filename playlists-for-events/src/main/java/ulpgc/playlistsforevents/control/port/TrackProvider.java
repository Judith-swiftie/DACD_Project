package ulpgc.playlistsforevents.control.port;

import java.util.List;

public interface TrackProvider {
    List<String> getTracksByArtist(String artistName);
}
