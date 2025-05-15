package ulpgc.ticketmasterfeeder.control.provider;

import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;

public interface MusicalEventProvider {
    List<Event> fetchMusicEvents();
}
