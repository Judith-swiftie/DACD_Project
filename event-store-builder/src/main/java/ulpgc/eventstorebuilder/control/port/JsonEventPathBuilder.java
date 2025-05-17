package ulpgc.eventstorebuilder.control.port;

import java.nio.file.Path;

public interface JsonEventPathBuilder {
    Path buildPath(String topicName, String json);
}
