package org.example;

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

    public JSONObject getArtistData(String artistName) throws Exception {
        String url = String.format(SEARCH_URL, artistName.replace(" ", "%20"));
        String response = client.sendGetRequest(url);
        JSONObject jsonResponse = new JSONObject(response);

        if (jsonResponse.has("artists")) {
            JSONArray artists = jsonResponse.getJSONObject("artists").getJSONArray("items");
            return !artists.isEmpty() ? artists.getJSONObject(0) : null;
        }
        return null;
    }

    public List<String> getTopTracksByCountry(String artistId, String countryCode) throws Exception {
        String url = String.format(TOP_TRACKS_URL, artistId, countryCode);
        String response = client.sendGetRequest(url);
        JSONObject jsonResponse = new JSONObject(response);

        List<String> tracksList = new ArrayList<>();

        if (jsonResponse.has("tracks")) {
            JSONArray tracks = jsonResponse.getJSONArray("tracks");
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                tracksList.add(trackName);
            }
        } else {
            System.out.println("⚠️ No se encontraron canciones populares.");
        }

        return tracksList;
    }

    public JSONObject createPlaylistFromArtist(String artistName, String countryCode) throws Exception {
        // Buscar el artista
        JSONObject artistData = getArtistData(artistName);

        if (artistData != null) {
            // Obtener las canciones más populares en el país
            String artistId = artistData.getString("id");
            List<String> topTracks = getTopTracksByCountry(artistId, countryCode);

            // Crear la playlist (en memoria, no asociada a ningún usuario)
            JSONObject playlist = new JSONObject();
            playlist.put("artist", artistName);
            playlist.put("country", countryCode);
            playlist.put("tracks", topTracks);

            System.out.println("\n🎼 Playlist creada para el artista " + artistName + ":");
            for (String track : topTracks) {
                System.out.println("🎶 " + track);
            }

            return playlist;
        } else {
            System.out.println("❌ No se encontró el artista.");
            return null;
        }
    }
}
