package ulpgc.playlistsforevents.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void testGetName() {
        String artistName = "Coldplay";
        Artist artist = new Artist(artistName);

        assertEquals(artistName, artist.getName());
    }
}
