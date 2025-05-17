package ulpgc.playlistsforevents.control.adapter.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulpgc.playlistsforevents.model.Playlist;
import ulpgc.playlistsforevents.model.Track;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistPresenterTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private PlaylistPresenter presenter;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        presenter = new PlaylistPresenter();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testShowGeneratingMessage() {
        presenter.showGeneratingMessage("Festival de Verano");
        String output = outputStream.toString().trim();
        assertEquals("Generando playlist para el evento: Festival de Verano", output);
    }

    @Test
    void testShowArtistProcessing() {
        presenter.showArtistProcessing("Rosalía");
        String output = outputStream.toString().trim();
        assertEquals("Artista: Rosalía", output);
    }


    @Test
    void testDisplayPlaylistWithTracks() {
        Playlist playlist = new Playlist("Fiesta Latina", Arrays.asList(
                new Track("Despacito"),
                new Track("Bailando")
        ));

        presenter.displayPlaylist(playlist);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Playlist Generada:"));
        assertTrue(output.contains("Nombre de la playlist: Fiesta Latina"));
        assertTrue(output.contains("  - Despacito"));
        assertTrue(output.contains("  - Bailando"));
    }

    @Test
    void testDisplayPlaylistWithoutTracks() {
        Playlist playlist = new Playlist("Silencio Total", Collections.emptyList());
        presenter.displayPlaylist(playlist);
        String output = outputStream.toString().trim();
        assertTrue(output.contains("Playlist Generada:"));
        assertTrue(output.contains("Nombre de la playlist: Silencio Total"));
        assertFalse(output.contains("  - "));
    }
}
