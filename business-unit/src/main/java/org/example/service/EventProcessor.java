package org.example.service;

import org.example.integration.SpotifyValidator;
import org.example.integration.TicketmasterReader;
import org.example.model.Event;

import java.util.List;
import java.util.stream.Collectors;

public class EventProcessor {
    private final TicketmasterReader reader;
    private final SpotifyValidator validator;

    public EventProcessor(TicketmasterReader reader, SpotifyValidator validator) {
        this.reader = reader;
        this.validator = validator;
    }

    public List<Event> getValidEvents() {
        return reader.loadAllEvents().stream()
                .filter(validator::allArtistsExistOnSpotify)
                .collect(Collectors.toList());
    }
}

