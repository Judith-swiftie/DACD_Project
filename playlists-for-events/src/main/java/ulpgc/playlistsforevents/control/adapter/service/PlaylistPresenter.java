package ulpgc.playlistsforevents.control.adapter.service;

import ulpgc.playlistsforevents.model.Playlist;
import ulpgc.playlistsforevents.model.Track;

public class PlaylistPresenter {

    public void showGeneratingMessage(String eventName) {
        System.out.println("Generando playlist para el evento: " + eventName);
    }

    public void showArtistProcessing(String artistName) {

        System.out.println("Artista: " + artistName);
    }

    public void showTrack(String title) {
        System.out.println("  Canción: " + title);
    }

    public void showNoTracks() {
        System.out.println("  No hay canciones disponibles para este artista.");
    }

    public void displayPlaylist(Playlist playlist) {
        System.out.println("\nPlaylist Generada:");
        System.out.println("Nombre de la playlist: " + playlist.getName());
        for (Track track : playlist.getTracks()) {
            System.out.println("  - " + track.getTitle());
        }
    }
}
