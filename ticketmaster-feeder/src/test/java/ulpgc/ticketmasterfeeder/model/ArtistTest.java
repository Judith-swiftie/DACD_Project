package ulpgc.ticketmasterfeeder.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArtistTest {

    @Test
    void testGetName() {
        Artist artist = new Artist("The Beatles");
        assertEquals("The Beatles", artist.getName());
    }
}
