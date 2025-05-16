package ulpgc.eventstorebuilder;

import java.nio.file.Path;

public interface JsonEventPathBuilder {
    Path buildPath(String topicName, String json);
}
