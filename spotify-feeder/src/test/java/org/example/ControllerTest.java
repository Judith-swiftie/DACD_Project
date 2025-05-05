package org.example;

import org.example.control.Controller;
import org.example.control.store.SqliteMusicStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {

    private SqliteMusicStore musicStore;
    private Controller feeder;

    @BeforeEach
    public void setUp() {
        musicStore = new SqliteMusicStore(System.getenv("DB_URL"));
        feeder = new Controller(System.getenv("DB_URL"));

        musicStore.createTables();

        musicStore.store("1", "Lady Gaga", List.of("Shallow", "Bad Romance"));
    }

    @Test
    public void testSendSpotifyEvents() {
        try {
            feeder.sendSpotifyEvents();

            assertTrue(true, "Feeder ejecutado correctamente");
        } catch (Exception e) {
            fail("Feeder execution failed: " + e.getMessage());
        }
    }
}
