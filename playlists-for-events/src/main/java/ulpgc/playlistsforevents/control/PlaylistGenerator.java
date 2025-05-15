package ulpgc.playlistsforevents.control;

import ulpgc.playlistsforevents.control.store.Datamart;
import ulpgc.playlistsforevents.model.Artist;
import ulpgc.playlistsforevents.model.Event;
import ulpgc.playlistsforevents.model.Playlist;
import ulpgc.playlistsforevents.model.Track;
import java.util.ArrayList;
import java.util.List;

public class PlaylistGenerator {
    private final Datamart datamart;

    public PlaylistGenerator(Datamart datamart) {
        this.datamart = datamart;
    }

    public void generatePlaylistForEvent(Event event) {
        announcePlaylistGeneration(event);
        List<Track> playlistTracks = buildTracksFromEvent(event);
        Playlist playlist = createPlaylist(event, playlistTracks);
        displayPlaylist(playlist);
    }

    private void announcePlaylistGeneration(Event event) {
        System.out.println("Generando playlist para el evento: " + event.getName());
    }

    private List<Track> buildTracksFromEvent(Event event) {
        List<Track> tracks = new ArrayList<>();
        for (Artist artist : event.getArtists()) {
            processArtist(artist, tracks);
        }
        return tracks;
    }

    private void processArtist(Artist artist, List<Track> tracks) {
        String artistName = artist.getName();
        System.out.println("Artista: " + artistName);
        List<String> artistTracks = datamart.getTracksByArtist(artistName);
        if (artistTracks.isEmpty()) {
            System.out.println("  No hay canciones disponibles para este artista.");
            return;
        }
        for (String title : artistTracks) {
            Track track = new Track(title, "spotify:track:exampleUri");
            tracks.add(track);
            System.out.println("  Canción: " + title);
        }
    }

    private Playlist createPlaylist(Event event, List<Track> tracks) {
        return new Playlist("Mi Playlist para el Evento: " + event.getName(), tracks);
    }

    private void displayPlaylist(Playlist playlist) {
        System.out.println("\nPlaylist Generada:");
        System.out.println("Nombre de la playlist: " + playlist.getName());
        for (Track track : playlist.getTracks()) {
            System.out.println("  - " + track.getTitle());
        }
    }
}