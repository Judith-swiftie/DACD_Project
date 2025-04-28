package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    private Event event;
    private long timestamp;
    private String source;
    private Map<String, Object> data;

    @BeforeEach
    public void setUp() {
        timestamp = 1633058400000L;  // Un timestamp de ejemplo (1 de octubre de 2021).
        source = "source1";
        data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        event = new Event(timestamp, source, data);
    }

    @Test
    public void testGetTimestamp() {
        assertEquals(timestamp, event.getTimestamp(), "El timestamp debe coincidir con el valor proporcionado.");
    }

    @Test
    public void testGetSource() {
        assertEquals(source, event.getSource(), "La fuente debe coincidir con el valor proporcionado.");
    }

    @Test
    public void testGetData() {
        assertEquals(data, event.getData(), "Los datos deben coincidir con el valor proporcionado.");
    }

    @Test
    public void testGetTs() {
        Instant expectedTs = Instant.ofEpochMilli(timestamp);
        assertEquals(expectedTs, event.getTs(), "El tiempo de la marca debe ser convertido correctamente a Instant.");
    }

    @Test
    public void testGetDataMap() {
        Map<String, Object> eventData = event.getData();
        assertNotNull(eventData, "Los datos del evento no deben ser nulos.");
        assertTrue(eventData.containsKey("key1"), "El mapa de datos debe contener la clave 'key1'.");
        assertEquals("value1", eventData.get("key1"), "El valor de 'key1' debe ser 'value1'.");
    }
}
