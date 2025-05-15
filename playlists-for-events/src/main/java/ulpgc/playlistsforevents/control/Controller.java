package ulpgc.playlistsforevents.control;

import ulpgc.playlistsforevents.control.store.Datamart;
import ulpgc.playlistsforevents.control.consumers.HistoricalEventLoader;
import ulpgc.playlistsforevents.control.consumers.RealTimeEventConsumer;
import ulpgc.playlistsforevents.control.consumers.SpotifyFeederLoader;
import ulpgc.playlistsforevents.model.Event;
import java.util.List;
import java.util.Optional;

public class Controller {
    private final RealTimeEventConsumer eventConsumer;
    private final HistoricalEventLoader historicalEventLoader;
    private final PlaylistGenerator playlistGenerator;
    private final Datamart datamart;
    private final EventSelector eventSelector;
    private final SpotifyFeederLoader spotifyFeederLoader;

    public Controller(
            RealTimeEventConsumer eventConsumer,
            HistoricalEventLoader historicalEventLoader,
            PlaylistGenerator playlistGenerator,
            Datamart datamart,
            EventSelector eventSelector,
            SpotifyFeederLoader spotifyFeederLoader) {
        this.eventConsumer = eventConsumer;
        this.historicalEventLoader = historicalEventLoader;
        this.playlistGenerator = playlistGenerator;
        this.datamart = datamart;
        this.eventSelector = eventSelector;
        this.spotifyFeederLoader = spotifyFeederLoader;
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
