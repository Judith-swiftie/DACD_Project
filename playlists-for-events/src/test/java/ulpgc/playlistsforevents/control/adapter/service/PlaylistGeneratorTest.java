package ulpgc.playlistsforevents.control.adapter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulpgc.playlistsforevents.control.port.TrackProvider;
import ulpgc.playlistsforevents.model.Artist;
import ulpgc.playlistsforevents.model.Event;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class PlaylistGeneratorTest {
    private TrackProvider trackProvider;
    private PlaylistPresenter presenter;
    private PlaylistGenerator generator;

    @BeforeEach
    void setUp() {
        trackProvider = mock(TrackProvider.class);
        presenter = mock(PlaylistPresenter.class);
        generator = new PlaylistGenerator(trackProvider, presenter);
    }

    @Test
    void generatePlaylistForEvent_generatesPlaylistWithTracks() {
        Artist artist = new Artist("Coldplay");
        Event event = new Event("Concierto Rock", List.of(artist));
        when(trackProvider.getTracksByArtist("Coldplay"))
                .thenReturn(Arrays.asList("Yellow", "Fix You"));
        generator.generatePlaylistForEvent(event);
        verify(presenter).showGeneratingMessage("Concierto Rock");
        verify(presenter).displayPlaylist(argThat(playlist ->
                playlist.getName().equals("Mi Playlist para el Evento: Concierto Rock") &&
                        playlist.getTracks().size() == 2 &&
                        playlist.getTracks().get(0).getTitle().equals("Yellow") &&
                        playlist.getTracks().get(1).getTitle().equals("Fix You")
        ));
    }

    @Test
    void generatePlaylistForEvent_withArtistWithoutTracks_generatesEmptyPlaylist() {
        Artist artist = new Artist("Artista Desconocido");
        Event event = new Event("Evento Vacío", List.of(artist));
        when(trackProvider.getTracksByArtist("Artista Desconocido"))
                .thenReturn(Collections.emptyList());
        generator.generatePlaylistForEvent(event);
        verify(presenter).showGeneratingMessage("Evento Vacío");
        verify(presenter).displayPlaylist(argThat(playlist ->
                playlist.getName().equals("Mi Playlist para el Evento: Evento Vacío") &&
                        playlist.getTracks().isEmpty()
        ));
    }
}
