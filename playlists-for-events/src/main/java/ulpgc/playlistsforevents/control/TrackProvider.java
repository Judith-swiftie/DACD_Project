package ulpgc.playlistsforevents.control;

import java.util.List;

public interface TrackProvider {
    List<String> getTracksByArtist(String artistName);
}
