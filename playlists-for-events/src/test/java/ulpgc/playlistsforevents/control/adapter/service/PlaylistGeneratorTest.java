package ulpgc.playlistsforevents.control.adapter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ulpgc.playlistsforevents.control.port.TrackProvider;
import ulpgc.playlistsforevents.model.Artist;
import ulpgc.playlistsforevents.model.Event;
import ulpgc.playlistsforevents.model.Playlist;
import ulpgc.playlistsforevents.model.Track;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
    void generatePlaylistForEvent_withTracksFromArtists() {
        Artist artist1 = new Artist("Coldplay");
        Artist artist2 = new Artist("Adele");
        Event event = new Event("Concierto Estelar", Arrays.asList(artist1, artist2));
        when(trackProvider.getTracksByArtist("Coldplay")).thenReturn(List.of("Yellow", "Fix You"));
        when(trackProvider.getTracksByArtist("Adele")).thenReturn(List.of("Hello"));
        generator.generatePlaylistForEvent(event);
        verify(presenter).showGeneratingMessage("Concierto Estelar");
        verify(presenter).showArtistProcessing("Coldplay");
        verify(presenter).showTrack("Yellow");
        verify(presenter).showTrack("Fix You");
        verify(presenter).showArtistProcessing("Adele");
        verify(presenter).showTrack("Hello");
        ArgumentCaptor<Playlist> playlistCaptor = ArgumentCaptor.forClass(Playlist.class);
        verify(presenter).displayPlaylist(playlistCaptor.capture());
        Playlist playlist = playlistCaptor.getValue();
        assertEquals("Mi Playlist para el Evento: Concierto Estelar", playlist.getName());
        List<Track> tracks = playlist.getTracks();
        assertEquals(3, tracks.size());
        assertEquals("Yellow", tracks.get(0).getTitle());
        assertEquals("Fix You", tracks.get(1).getTitle());
        assertEquals("Hello", tracks.get(2).getTitle());
    }

    @Test
    void generatePlaylistForEvent_withNoTracksForArtist() {
        Artist artist = new Artist("Unknown Artist");
        Event event = new Event("Evento Sin Canciones", List.of(artist));
        when(trackProvider.getTracksByArtist("Unknown Artist")).thenReturn(Collections.emptyList());
        generator.generatePlaylistForEvent(event);
        verify(presenter).showGeneratingMessage("Evento Sin Canciones");
        verify(presenter).showArtistProcessing("Unknown Artist");
        verify(presenter).showNoTracks();
        ArgumentCaptor<Playlist> playlistCaptor = ArgumentCaptor.forClass(Playlist.class);
        verify(presenter).displayPlaylist(playlistCaptor.capture());
        Playlist playlist = playlistCaptor.getValue();
        assertEquals("Mi Playlist para el Evento: Evento Sin Canciones", playlist.getName());
        assertTrue(playlist.getTracks().isEmpty());
    }
}
