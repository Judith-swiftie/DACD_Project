package ulpgc.ticketmasterfeeder.control.port;

import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;

public interface MusicalEventProvider {
    List<Event> fetchMusicEvents();
}
