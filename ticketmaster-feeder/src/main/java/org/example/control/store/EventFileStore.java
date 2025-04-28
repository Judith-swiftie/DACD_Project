package org.example.control.store;

import com.google.gson.Gson;
import org.example.Event;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class EventFileStore {

    private final String basePath = "eventstore";
    private final Gson gson = new Gson();

    public void storeEvent(String topic, Event event) {
        System.out.println("🔄 Ejecutando storeEvent() para el evento: " + event);

        String date = DateTimeFormatter.ofPattern("yyyyMMdd")
                .withZone(ZoneOffset.UTC)
                .format(event.getTs());

        String filePath = Paths.get(basePath, topic, event.getSource(), date + ".events").toString();

        System.out.println("📝 Guardando en: " + filePath);

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(gson.toJson(event) + "\n");
        } catch (IOException e) {
            System.err.println("❌ Error guardando evento: " + e.getMessage());
        }
    }
}
