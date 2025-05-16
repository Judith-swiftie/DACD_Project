package ulpgc.spotifyfeeder.control.port;

import java.util.List;

public interface TrackProvider {
    List<String> getTopTracksByCountry(String artistId, String countryCode) throws Exception;
}
