package ulpgc.spotifyfeeder.control.port;

import java.util.List;

public interface MusicStore {
    void store(String artistId, String artistName, List<String> tracks);
    List<String> getTracksByArtistId(String artistId);
    boolean hasTracksChanged(String artistId, List<String> newTracks);
}
