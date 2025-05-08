package org.example;

import org.example.consumer.HistoricalEventLoader;
import org.example.consumer.RealTimeEventConsumer;
import org.example.datamart.Datamart;
import org.example.model.Event;

import java.util.List;

public class BusinessUnit {

    private final RealTimeEventConsumer eventConsumer;
    private final HistoricalEventLoader historicalEventLoader;
    private final PlaylistGenerator playlistGenerator;
    private final Datamart datamart;

    public BusinessUnit(String topicName, String eventStorePath) {
        this.historicalEventLoader = new HistoricalEventLoader(eventStorePath);
        this.playlistGenerator = new PlaylistGenerator();
        this.datamart = new Datamart();
        this.eventConsumer = new RealTimeEventConsumer(topicName);

    }

    public void start() {
        eventConsumer.startConsumingEvents();
    }

    public void loadHistoricalEvents() {
        List<Event> historicalEvents = historicalEventLoader.loadEvents();
        playlistGenerator.generatePlaylistsFromEvents(historicalEvents);
        for (Event event : historicalEvents) {
            datamart.addEvent(event);
        }
    }

    public void generatePlaylistForEvent(Event event) {
        playlistGenerator.generatePlaylistForEvent(event);
    }
}
