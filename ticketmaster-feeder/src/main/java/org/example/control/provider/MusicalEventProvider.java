package org.example.control.provider;

import java.util.List;

public interface MusicalEventProvider {
    List<org.example.model.Event> fetchMusicEvents();
}
