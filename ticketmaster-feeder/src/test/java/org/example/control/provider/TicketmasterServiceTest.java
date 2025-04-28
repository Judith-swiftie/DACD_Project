package org.example.control.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketmasterServiceTest {

    private TicketmasterService ticketmasterService;

    @BeforeEach
    void setUp() {
        ticketmasterService = new TicketmasterService();
    }

    @Test
    void testFetchMusicEvents() {
        List<Event> events = ticketmasterService.fetchMusicEvents();

        assertNotNull(events);
        assertTrue(events.size() > 0, "Debe haber al menos un evento");
    }
}
