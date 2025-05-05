package org.example.control.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketMasterEventProviderTest {

    private TicketMasterEventProvider ticketMasterEventProvider;

    @BeforeEach
    void setUp() {
        ticketMasterEventProvider = new TicketMasterEventProvider();
    }

    @Test
    void testFetchMusicEvents() {
        List<Event> events = ticketMasterEventProvider.fetchMusicEvents();

        assertNotNull(events);
        assertTrue(events.size() > 0, "Debe haber al menos un evento");
    }
}
