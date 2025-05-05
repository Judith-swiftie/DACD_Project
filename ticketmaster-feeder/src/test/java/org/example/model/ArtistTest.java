package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void testArtistName() {
        Artist artist = new Artist("Banda X");

        assertEquals("Banda X", artist.getName());
    }
}
