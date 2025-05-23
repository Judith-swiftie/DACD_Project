package ulpgc.playlistsforevents.control.adapter.service;

import ulpgc.playlistsforevents.control.port.TrackProvider;
import ulpgc.playlistsforevents.model.Artist;
import ulpgc.playlistsforevents.model.Event;
import ulpgc.playlistsforevents.model.Playlist;
import ulpgc.playlistsforevents.model.Track;
import java.util.ArrayList;
import java.util.List;

public class PlaylistGenerator {
    private final TrackProvider trackProvider;
    private final PlaylistPresenter presenter;

    public PlaylistGenerator(TrackProvider trackProvider, PlaylistPresenter presenter) {
        this.trackProvider = trackProvider;
        this.presenter = presenter;
    }

    public void generatePlaylistForEvent(Event event) {
        presenter.showGeneratingMessage(event.getName());
        List<Track> playlistTracks = buildTracksFromEvent(event);
        Playlist playlist = new Playlist("Mi Playlist para el Evento: " + event.getName(), playlistTracks);
        presenter.displayPlaylist(playlist);
    }

    private List<Track> buildTracksFromEvent(Event event) {
        List<Track> tracks = new ArrayList<>();
        for (Artist artist : event.getArtists()) {
            String artistName = artist.getName();
            List<String> artistTracks = trackProvider.getTracksByArtist(artistName);
            if (!artistTracks.isEmpty()) {
                for (String title : artistTracks) {
                    tracks.add(new Track(title));
                }
            }
        }
        return tracks;
    }
}
