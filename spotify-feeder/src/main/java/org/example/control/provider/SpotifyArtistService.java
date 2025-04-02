package org.example.control.provider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpotifyArtistService {
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search?q=%s&type=artist";
    private static final String TOP_TRACKS_URL = "https://api.spotify.com/v1/artists/%s/top-tracks?market=%s";
    private final SpotifyClient client;

    public SpotifyArtistService(SpotifyClient client) {
        this.client = client;
    }

    public JSONObject findArtistByName(String artistName) throws Exception {
        String url = String.format(SEARCH_URL, artistName.replace(" ", "%20"));
        String response = client.sendGetRequest(url);
        JSONObject jsonResponse = new JSONObject(response);

        if (!jsonResponse.has("artists")) {
            System.out.println("⚠️ La respuesta no contiene el objeto 'artists'.");
            return null;
        }

        JSONArray artists = jsonResponse.getJSONObject("artists").getJSONArray("items");
        if (artists.isEmpty()) {
            System.out.println("⚠️ No se encontraron artistas en la respuesta.");
            return null;
        }

        JSONObject artist = artists.getJSONObject(0);
        if (!artist.has("id")) {
            System.out.println("⚠️ El objeto artista no contiene el campo 'id'.");
            return null;
        }

        return artist;
    }

    public List<String> getTopTracksByCountry(String artistId, String countryCode) throws Exception {
        String url = String.format(TOP_TRACKS_URL, artistId, countryCode);
        String response = client.sendGetRequest(url);
        JSONObject jsonResponse = new JSONObject(response);

        List<String> tracksList = new ArrayList<>();

        if (!jsonResponse.has("tracks")) {
            System.out.println("⚠️ No se encontraron canciones populares en la respuesta.");
            return tracksList;
        }

        JSONArray tracks = jsonResponse.getJSONArray("tracks");
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            String trackName = track.optString("name", "Desconocido");
            tracksList.add(trackName);
        }

        return tracksList;
    }
}
