package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;
import java.util.Map;

public class FileJsonEventStore implements JsonEventStore {

    private static final Path BASE_DIRECTORY = Paths.get("eventstore");
    private final String topicName;
    private final Gson gson = new Gson();

    public FileJsonEventStore(String topicName) {
        this.topicName = topicName;
        System.out.println("topicName: " + topicName);
    }

    @Override
    public void saveJson(String json) {
        try {
            Map<String, Object> eventMap = gson.fromJson(json, Map.class);
            String ss = (String) eventMap.get("ss");
            String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

            Path topicDir = BASE_DIRECTORY.resolve(topicName);
            Path ssDir = topicDir.resolve(ss);
            Path filePath = ssDir.resolve(dateStr + ".events");

            if (!Files.exists(ssDir)) {
                Files.createDirectories(ssDir);
            }

            Files.writeString(filePath, json + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            System.out.println("💾 JSON guardado en: " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Error al guardar JSON: " + e.getMessage());
        }
    }
}
