package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void testArtistConstructor() {
        Artist artist = new Artist("Artist Name", "Pop", "Bio of artist");

        assertEquals("Artist Name", artist.getName());
        assertEquals("Pop", artist.getGenre());
        assertEquals("Bio of artist", artist.getBio());
    }

    @Test
    void testSettersAndGetters() {
        Artist artist = new Artist("Artist Name", "Pop", "Bio of artist");

        artist.setName("New Artist");
        artist.setGenre("Rock");
        artist.setBio("New bio");

        assertEquals("New Artist", artist.getName());
        assertEquals("Rock", artist.getGenre());
        assertEquals("New bio", artist.getBio());
    }

    @Test
    void testToString() {
        Artist artist = new Artist("Artist Name", "Pop", null);
        assertEquals("🎤 Artista: Artist Name\n🎶 Género: Pop\n📝 Biografía: No disponible", artist.toString());
    }
}
