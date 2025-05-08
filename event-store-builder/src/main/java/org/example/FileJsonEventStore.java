package org.example;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import com.google.gson.Gson;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
            Path filePath = buildFilePathFromJson(json);
            writeJsonToFile(filePath, json);
            System.out.println("JSON guardado en: " + filePath);
        } catch (IOException e) {
            System.err.println("---Error al guardar JSON: " + e.getMessage());
        }
    }

    private Path buildFilePathFromJson(String json) {
        Map<String, Object> eventMap = gson.fromJson(json, Map.class);
        String ss = (String) eventMap.get("ss");
        String dateStr = ZonedDateTime.parse((String) eventMap.get("ts"))
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path topicDir = BASE_DIRECTORY.resolve(topicName);
        Path ssDir = topicDir.resolve(ss);
        if (!Files.exists(ssDir)) {
            try {
                Files.createDirectories(ssDir);
            } catch (IOException e) {
                throw new UncheckedIOException("No se pudo crear el directorio: " + ssDir, e);
            }
        }
        return ssDir.resolve(dateStr + ".events");
    }

    private void writeJsonToFile(Path filePath, String json) throws IOException {
        Files.writeString(filePath, json + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
