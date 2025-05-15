package ulpgc.playlistsforevents;

import ulpgc.playlistsforevents.control.*;
import ulpgc.playlistsforevents.control.consumers.*;
import ulpgc.playlistsforevents.control.store.Datamart;

public class Main {
    public static void main(String[] args) {
        String eventStorePath = "eventstore/events/TicketmasterFeeder";
        String spotifyFeederPath = "eventstore/playlist/SpotifyFeeder";
        String topicName = "events";
        Datamart datamart = new Datamart();
        HistoricalEventLoader historicalEventLoader = new HistoricalEventLoader(eventStorePath);
        SpotifyFeederLoader spotifyFeederLoader = new SpotifyFeederLoader(spotifyFeederPath, datamart);
        PlaylistGenerator playlistGenerator = new PlaylistGenerator(datamart);
        RealTimeEventConsumer eventConsumer = new RealTimeEventConsumer(topicName, datamart);
        EventSelector eventSelector = new EventSelector(datamart);
        Controller coreController = new Controller(eventConsumer, historicalEventLoader, playlistGenerator, datamart, eventSelector, spotifyFeederLoader);
        Controller2 uiController = new Controller2(coreController);
        uiController.runUserInterface();
    }
}
