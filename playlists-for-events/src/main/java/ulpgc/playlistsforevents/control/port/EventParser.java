package ulpgc.playlistsforevents.control.port;

import ulpgc.playlistsforevents.model.Event;
import java.util.Optional;

public interface EventParser {
    Optional<Event> parse(String json);
    Event fromJson(String json);
    String toJson(Event event);
}
