package ulpgc.eventstorebuilder;

import java.io.IOException;
import java.nio.file.Path;

public class FileJsonEventStore implements JsonEventStore {
    private final String topicName;
    private final JsonEventPathBuilder pathBuilder;
    private final FileWriter fileWriter;

    public FileJsonEventStore(String topicName,
                              JsonEventPathBuilder pathBuilder,
                              FileWriter fileWriter) {
        this.topicName = topicName;
        this.pathBuilder = pathBuilder;
        this.fileWriter = fileWriter;
    }

    @Override
    public void saveJson(String json) {
        try {
            Path filePath = pathBuilder.buildPath(topicName, json);
            fileWriter.append(filePath, json);
            System.out.println("JSON guardado en: " + filePath);
        } catch (IOException e) {
            System.err.println("---Error al guardar JSON: " + e.getMessage());
        }
    }
}
