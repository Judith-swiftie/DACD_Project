package org.example.control.provider;

import org.json.JSONObject;

import java.util.List;

public interface ServiceStore {
    JSONObject findArtistByName(String artistName) throws Exception;
    List<String> getTopTracksByCountry(String artistId, String countryCode) throws Exception;
}
