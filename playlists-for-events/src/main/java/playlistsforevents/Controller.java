package playlistsforevents;

import playlistsforevents.consumers.HistoricalEventLoader;
import playlistsforevents.consumers.RealTimeEventConsumer;
import playlistsforevents.consumers.SpotifyFeederLoader;
import playlistsforevents.model.Event;

import java.util.List;
import java.util.Optional;

public class Controller {

    private final RealTimeEventConsumer eventConsumer;
    private final HistoricalEventLoader historicalEventLoader;
    private final PlaylistGenerator playlistGenerator;
    private final Datamart datamart;
    private final EventSelector eventSelector;
    private final SpotifyFeederLoader spotifyFeederLoader;

    public Controller(String topicName, String eventStorePath, String spotifyFeederPath) {
        this.historicalEventLoader = new HistoricalEventLoader(eventStorePath);
        this.datamart = new Datamart();
        this.playlistGenerator = new PlaylistGenerator(datamart);
        this.eventConsumer = new RealTimeEventConsumer(topicName, datamart);
        this.eventSelector = new EventSelector(datamart);
        this.spotifyFeederLoader = new SpotifyFeederLoader(spotifyFeederPath, datamart);
    }

    public void start() {
        eventConsumer.startConsumingEvents();
    }

    public void loadHistoricalEvents() {
        List<Event> events = historicalEventLoader.loadEvents();
        events.forEach(datamart::addEvent);
    }

    public void loadSpotifyData() {
        spotifyFeederLoader.loadArtistTracks();
    }

    public void interactWithUser() {
        loadHistoricalEvents();
        Optional<Event> selectedEvent = eventSelector.selectEventFromUserInput();
        if (selectedEvent.isEmpty()) {
            System.out.println("No se seleccionó ningún evento.");
            return;
        }

        playlistGenerator.generatePlaylistForEvent(selectedEvent.get());
    }

    public List<Event> searchEventsByName(String keyword) {
        return datamart.searchByName(keyword);
    }

    public List<Event> searchEventsByArtist(String artistName) {
        return datamart.searchByArtist(artistName);
    }

    public void generatePlaylistForEvent(Event event) {
        playlistGenerator.generatePlaylistForEvent(event);
    }
}
