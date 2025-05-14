package playlistsforevents;

import playlistsforevents.model.Artist;
import playlistsforevents.model.Event;
import playlistsforevents.model.Playlist;
import playlistsforevents.model.Track;

import java.util.ArrayList;
import java.util.List;

public class PlaylistGenerator {

    private final Datamart datamart;

    public PlaylistGenerator(Datamart datamart) {
        this.datamart = datamart;
    }

    public void generatePlaylistForEvent(Event event) {
        List<Track> playlistTracks = new ArrayList<>();
        System.out.println("Generando playlist para el evento: " + event.getName());

        for (Artist artist : event.getArtists()) {
            String artistName = artist.getName();
            System.out.println("Artista: " + artistName);

            List<String> tracks = datamart.getTracksByArtist(artistName);
            if (tracks.isEmpty()) {
                System.out.println("  No hay canciones disponibles para este artista.");
                continue;
            }

            for (String trackTitle : tracks) {
                Track track = new Track(trackTitle, "spotify:track:exampleUri");
                playlistTracks.add(track);
                System.out.println("  Canción: " + track.getTitle());
            }
        }

        Playlist playlist = new Playlist("Mi Playlist para el Evento: " + event.getName(), playlistTracks);
        System.out.println("\nPlaylist Generada:");
        System.out.println("Nombre de la playlist: " + playlist.getName());
        for (Track track : playlist.getTracks()) {
            System.out.println("  - " + track.getTitle());
        }
    }
}
