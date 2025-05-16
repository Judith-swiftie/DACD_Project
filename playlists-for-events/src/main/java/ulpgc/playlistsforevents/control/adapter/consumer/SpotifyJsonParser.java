package ulpgc.playlistsforevents.control.adapter.consumer;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SpotifyJsonParser {
    public String parseArtistName(String jsonLine) {
        JSONObject node = new JSONObject(jsonLine);
        return node.getString("artistName");
    }

    public List<String> parseTracks(String jsonLine) {
        JSONObject node = new JSONObject(jsonLine);
        JSONArray trackArray = node.getJSONArray("tracks");
        List<String> tracks = new ArrayList<>();
        for (int i = 0; i < trackArray.length(); i++) {
            tracks.add(trackArray.getString(i));
        }
        return tracks;
    }
}
