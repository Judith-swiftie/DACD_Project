package ulpgc.playlistsforevents.control.adapter.consumer;

import ulpgc.playlistsforevents.control.port.EventParser;
import ulpgc.playlistsforevents.model.Event;
import java.util.Optional;

public class JsonEventParser implements EventParser {

    @Override
    public Optional<Event> parse(String json) {
        try {
            return Optional.of(Event.fromJson(json));
        } catch (Exception e) {
            System.err.println("Error parseando JSON: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Event fromJson(String json) {
        try {
            return Event.fromJson(json);
        } catch (Exception e) {
            throw new RuntimeException("Error parseando JSON a Event", e);
        }
    }

    @Override
    public String toJson(Event event) {
        try {
            return event.toJson();
        } catch (Exception e) {
            throw new RuntimeException("Error serializando Event a JSON", e);
        }
    }
}

