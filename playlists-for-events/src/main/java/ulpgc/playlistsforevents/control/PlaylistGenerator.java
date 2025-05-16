package ulpgc.playlistsforevents.control;

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
            presenter.showArtistProcessing(artistName);
            List<String> artistTracks = trackProvider.getTracksByArtist(artistName);
            if (artistTracks.isEmpty()) {
                presenter.showNoTracks();
            } else {
                for (String title : artistTracks) {
                    tracks.add(new Track(title));
                    presenter.showTrack(title);
                }
            }
        }
        return tracks;
    }
}
