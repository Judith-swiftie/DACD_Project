package ulpgc.playlistsforevents;

import org.apache.activemq.ActiveMQConnectionFactory;
import ulpgc.playlistsforevents.control.adapter.consumer.*;
import ulpgc.playlistsforevents.control.adapter.service.*;
import ulpgc.playlistsforevents.control.port.ConnectionProvider;
import ulpgc.playlistsforevents.control.adapter.store.Datamart;
import ulpgc.playlistsforevents.control.port.EventParser;
import ulpgc.playlistsforevents.control.port.EventProcessor;

import javax.jms.ConnectionFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        String eventStorePath = "eventstore/events/TicketmasterFeeder";
        String spotifyFeederPath = "eventstore/playlist/SpotifyFeeder";
        String topicName = "events";
        ConnectionProvider connectionProvider = new ConnectionProvider() {
            private final String DB_URL = System.getenv("DB_URL");

            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(DB_URL);
            }
        };
        EventParser parser = new JsonEventParser();
        Datamart datamart = new Datamart(connectionProvider, parser);
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        EventProcessor processor = new DatamartEventProcessor(datamart);
        RealTimeEventConsumer eventConsumer = new RealTimeEventConsumer(factory, topicName, processor);
        HistoricalEventLoader historicalEventLoader = new HistoricalEventLoader(eventStorePath, parser);
        FileReaderService fileReaderService = new FileReaderService();
        SpotifyJsonParser spotifyJsonParser = new SpotifyJsonParser();
        SpotifyFeederLoader spotifyFeederLoader = new SpotifyFeederLoader(spotifyFeederPath, datamart, fileReaderService, spotifyJsonParser);
        PlaylistPresenter presenter = new PlaylistPresenter();
        PlaylistGenerator playlistGenerator = new PlaylistGenerator(datamart, presenter);
        EventSelector eventSelector = new EventSelector(datamart);
        Controller coreController = new Controller(eventConsumer, historicalEventLoader, playlistGenerator, datamart, eventSelector, spotifyFeederLoader);
        InterfaceController uiController = new InterfaceController(coreController);
        uiController.runUserInterface();
    }
}
