package ulpgc.eventstorebuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DefaultFileWriter implements FileWriter {
    @Override
    public void append(Path path, String content) throws IOException {
        ensureDirectoryExists(path.getParent());
        Files.writeString(path, content + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void ensureDirectoryExists(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new UncheckedIOException("No se pudo crear el directorio: " + directory, e);
            }
        }
    }
}
