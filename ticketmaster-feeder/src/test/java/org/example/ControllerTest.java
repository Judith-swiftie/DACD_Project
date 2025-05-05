package org.example;

import org.example.control.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private Controller controller;

    @BeforeEach
    void setUp() {
        controller = new Controller();
    }

    @Test
    void testSendTicketmasterEventToBroker() throws Exception {
        Method method = Controller.class.getDeclaredMethod("sendTicketmasterEventToBroker", String.class, String.class);
        method.setAccessible(true);
        method.invoke(controller, "Event Name", "Description");
        assertTrue(true, "Método invocado correctamente.");
    }
}
