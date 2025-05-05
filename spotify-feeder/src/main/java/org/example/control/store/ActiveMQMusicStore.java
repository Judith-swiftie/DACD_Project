package org.example.control.store;

import com.google.gson.Gson;
import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.control.provider.SpotifyArtistService;
import java.util.List;
import java.util.Map;
import java.time.Instant;

public class ActiveMQMusicStore implements MusicStore {

    private final String url;
    private final String sourceName = "SpotifyFeeder";
    private final ConnectionFactory connectionFactory;
    private final Gson gson = new Gson();
    private final SpotifyArtistService spotifyArtistService;

    public ActiveMQMusicStore(String url, SpotifyArtistService spotifyArtistService) {
        this.url = url;
        this.connectionFactory = new ActiveMQConnectionFactory(url);
        this.spotifyArtistService = spotifyArtistService;
    }

    @Override
    public void store(String artistId, String artistName, List<String> tracks) {
        try (Connection connection = connectionFactory.createConnection()) {
            connection.start();

            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

                Topic topic = session.createTopic(getTopicForArtist(artistName));

                try (MessageProducer producer = session.createProducer(topic)) {
                    String json = wrapArtistAsJson(artistId, artistName, tracks);
                    TextMessage message = session.createTextMessage(json);
                    producer.send(message);
                    System.out.println("📤 Evento enviado a topic '" + topic.getTopicName() + "': " + artistName);
                }
            }
        } catch (JMSException e) {
            System.err.println("❌ Error al enviar eventos a ActiveMQ: " + e.getMessage());
        }
    }

    private String wrapArtistAsJson(String artistId, String artistName, List<String> tracks) {
        Map<String, Object> map = Map.of(
                "ts", Instant.now().toString(),
                "ss", sourceName,
                "artistId", artistId,
                "artistName", artistName,
                "tracks", tracks
        );
        return gson.toJson(map);
    }

    private String getTopicForArtist(String artistName) {
        return "playlist";
    }

    @Override
    public List<String> getTracksByArtistId(String artistId) {
        try {
            return spotifyArtistService.getTopTracksByCountry(artistId, "ES");
        } catch (Exception e) {
            System.err.println("❌ Error al obtener canciones de Spotify: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean hasTracksChanged(String artistId, List<String> newTracks) {
        List<String> currentTracks = getTracksByArtistId(artistId);
        return !currentTracks.equals(newTracks);
    }
}
