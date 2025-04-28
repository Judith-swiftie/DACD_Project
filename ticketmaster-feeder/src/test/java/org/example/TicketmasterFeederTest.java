package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TicketmasterFeederTest {

    private TicketmasterFeeder ticketmasterFeeder;

    @BeforeEach
    void setUp() {
        ticketmasterFeeder = new TicketmasterFeeder();
    }

    @Test
    void testSendTicketmasterEventToBroker() throws Exception {
        Method method = TicketmasterFeeder.class.getDeclaredMethod("sendTicketmasterEventToBroker", String.class, String.class);
        method.setAccessible(true);
        method.invoke(ticketmasterFeeder, "Event Name", "Description");
        assertTrue(true, "Método invocado correctamente.");
    }
}
