package ulpgc.eventstorebuilder.control.port;

import java.io.IOException;
import java.nio.file.Path;

public interface FileWriter {
    void append(Path path, String content) throws IOException;
}
