import org.example.model.Artist;
import org.example.model.Track;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testArtist() {
        Artist artist = new Artist("06HL4z0CvFAxyc27GXpf02", "Taylor Swift");
        assertNotNull(artist);
        assertEquals("Taylor Swift", artist.getName());
    }

    @Test
    void testTrack() {
        Artist artist = new Artist("06HL4z0CvFAxyc27GXpf02", "Taylor Swift");
        Track track = new Track("1", "Cruel Summer", artist);

        assertNotNull(track);
        assertEquals("Cruel Summer", track.getName());
        assertEquals("Taylor Swift", track.getArtist().getName());
    }
}
