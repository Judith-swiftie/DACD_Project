package ulpgc.spotifyfeeder.control.provider;

import java.util.List;

public interface TrackProvider {
    List<String> getTopTracksByCountry(String artistId, String countryCode) throws Exception;
}
