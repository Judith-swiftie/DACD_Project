package ulpgc.ticketmaster.control.provider;

import ulpgc.ticketmaster.model.Event;

import java.util.List;

public interface MusicalEventProvider {
    List<Event> fetchMusicEvents();
}
