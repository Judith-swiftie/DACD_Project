package ulpgc.eventstorebuilder;

import com.google.gson.Gson;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DefaultJsonEventPathBuilder implements JsonEventPathBuilder {
    private static final Path BASE_DIRECTORY = Paths.get("eventstore");
    private final Gson gson = new Gson();

    @Override
    public Path buildPath(String topicName, String json) {
        Map<String, Object> eventMap = gson.fromJson(json, Map.class);
        String ss = (String) eventMap.get("ss");
        String dateStr = ZonedDateTime.parse((String) eventMap.get("ts"))
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return BASE_DIRECTORY.resolve(topicName).resolve(ss).resolve(dateStr + ".events");
    }
}
