package ulpgc.spotifyfeeder.control.adapter.store;

import com.google.gson.Gson;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import ulpgc.spotifyfeeder.control.port.MusicStore;
import ulpgc.spotifyfeeder.control.port.TrackProvider;
import java.util.List;
import java.util.Map;
import java.time.Instant;

public class ActiveMQMusicStore implements MusicStore {
    private final ConnectionFactory connectionFactory;
    private final Gson gson = new Gson();
    private final TrackProvider trackProvider = (artistId, countryCode) -> List.of();

    public ActiveMQMusicStore(String url) {
        this.connectionFactory = new ActiveMQConnectionFactory(url);
    }

    @Override
    public void store(String artistId, String artistName, List<String> tracks) {
        try (Connection connection = createConnection()) {
            connection.start();
            try (Session session = createSession(connection)) {
                Topic topic = createTopic(session, getTopicForArtist());
                sendTrackMessage(session, topic, artistId, artistName, tracks);
            }
        } catch (JMSException e) {
            System.err.println("Error al enviar eventos a ActiveMQ: " + e.getMessage());
        }
    }

    private Connection createConnection() throws JMSException {
        return connectionFactory.createConnection();
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Topic createTopic(Session session, String topicName) throws JMSException {
        return session.createTopic(topicName);
    }

    private void sendTrackMessage(Session session, Topic topic, String artistId, String artistName, List<String> tracks) throws JMSException {
        try (MessageProducer producer = session.createProducer(topic)) {
            String json = wrapArtistAsJson(artistId, artistName, tracks);
            TextMessage message = session.createTextMessage(json);
            producer.send(message);
            System.out.println("Evento enviado a topic '" + topic.getTopicName() + "': " + artistName);
        }
    }

    private String wrapArtistAsJson(String artistId, String artistName, List<String> tracks) {
        String sourceName = "SpotifyFeeder";
        Map<String, Object> map = Map.of(
                "ts", Instant.now().toString(),
                "ss", sourceName,
                "artistId", artistId,
                "artistName", artistName,
                "tracks", tracks
        );
        return gson.toJson(map);
    }

    private String getTopicForArtist() {
        return "playlist";
    }

    @Override
    public List<String> getTracksByArtistId(String artistId) {
        try {
            return trackProvider.getTopTracksByCountry(artistId, "ES");
        } catch (Exception e) {
            System.err.println("---Error al obtener canciones de Spotify: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean hasTracksChanged(String artistId, List<String> newTracks) {
        List<String> currentTracks = getTracksByArtistId(artistId);
        return !currentTracks.equals(newTracks);
    }

}
