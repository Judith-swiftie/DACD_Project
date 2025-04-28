package org.example.control.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteMusicStoreTest {
    private SqliteMusicStore store;

    @BeforeEach
    public void setUp() {
        // Recuperamos la URL de la base de datos desde la variable de entorno
        String dbUrl = System.getenv("DB_URL");
        assertNotNull(dbUrl, "La variable de entorno DB_URL debe estar definida");

        // Inicializamos el store con esa URL
        store = new SqliteMusicStore(dbUrl);
    }

    @Test
    public void testStoreArtistAndTracks() {
        assertDoesNotThrow(() -> {
            store.store("12345", "Lady Gaga", List.of("Shallow", "Bad Romance"));
        }, "Guardar artista y canciones no debería lanzar excepciones.");

        List<String> tracks = store.getTracksByArtistId("12345");

        assertNotNull(tracks, "La lista de tracks no debe ser nula.");
        assertEquals(2, tracks.size(), "Debe haber 2 tracks almacenados.");
        assertTrue(tracks.contains("Shallow"), "Debe contener 'Shallow'.");
        assertTrue(tracks.contains("Bad Romance"), "Debe contener 'Bad Romance'.");
    }

    @Test
    public void testHasTracksChanged() {
        assertDoesNotThrow(() -> {
            store.store("12345", "Lady Gaga", List.of("Shallow"));
        }, "Guardar artista y tracks no debería lanzar excepciones.");

        boolean changed = store.hasTracksChanged("12345", List.of("Shallow", "Abracadabra"));

        assertTrue(changed, "Debe detectar que las canciones han cambiado.");
    }
}
