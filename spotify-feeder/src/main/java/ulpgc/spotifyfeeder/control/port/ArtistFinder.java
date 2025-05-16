package ulpgc.spotifyfeeder.control.port;

import org.json.JSONObject;

public interface ArtistFinder {
    JSONObject findArtistByName(String artistName) throws Exception;
}
