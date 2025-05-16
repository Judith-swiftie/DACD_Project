package ulpgc.spotifyfeeder.control.provider;

import org.json.JSONObject;

public interface ArtistFinder {
    JSONObject findArtistByName(String artistName) throws Exception;
}
