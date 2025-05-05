package org.example.control;

import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.google.gson.Gson;
import org.example.control.store.MusicStore;
import org.example.control.store.SqliteMusicStore;
import org.example.model.Artist;

import java.util.List;
import java.util.Map;

public class Controller {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String TOPIC = "playlist";
    private static final String CLIENT_ID = "spotifyFeeder";

    private final MusicStore musicStore;
    private final ConnectionFactory factory;
    private final Gson gson;

    public Controller(String dbUrl) {
        this.musicStore = new SqliteMusicStore(dbUrl);
        this.factory = new ActiveMQConnectionFactory(BROKER_URL);
        this.gson = new Gson();
    }

    public List<Artist> getArtistsFromDatabase() {
        return ((SqliteMusicStore) musicStore).getAllArtists();
    }

    public void sendSpotifyEvents() {
        try (Connection connection = factory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageProducer producer = session.createProducer(session.createTopic(TOPIC))) {

            connection.start();

            List<Artist> artists = getArtistsFromDatabase();

            for (Artist artist : artists) {
                List<String> trackNames = musicStore.getTracksByArtistId(artist.getId());

                Event event = new Event(System.currentTimeMillis(), "SpotifyFeeder",
                        Map.of("artist", artist.getName(), "tracks", trackNames));
                String jsonEvent = gson.toJson(event);

                TextMessage message = session.createTextMessage(jsonEvent);
                producer.send(message);

                System.out.println("📡 Evento enviado al topic [" + TOPIC + "]:");
                System.out.println("    ➤ " + jsonEvent);
            }

        } catch (JMSException e) {
            System.err.println("❌ Error enviando evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
