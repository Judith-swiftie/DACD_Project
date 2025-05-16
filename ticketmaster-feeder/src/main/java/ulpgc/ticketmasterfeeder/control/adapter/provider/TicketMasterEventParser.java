package ulpgc.ticketmasterfeeder.control.adapter.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ulpgc.ticketmasterfeeder.model.Artist;
import ulpgc.ticketmasterfeeder.model.Event;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventParser {
    private final ObjectMapper objectMapper;

    public TicketMasterEventParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Event> parseEventsFromBody(String body) throws Exception {
        List<Event> events = new ArrayList<>();
        JsonNode root = objectMapper.readTree(body);
        JsonNode eventsNode = root.path("_embedded").path("events");
        if (eventsNode.isArray()) {
            for (JsonNode node : eventsNode) {
                Event e = parseEvent(node);
                if (e != null) events.add(e);
            }
        }
        return events;
    }

    private Event parseEvent(JsonNode event) {
        if (!isMusicEvent(event)) return null;
        String name = event.path("name").asText();
        String date = event.path("dates").path("start").path("localDate").asText();
        String time = event.path("dates").path("start").path("localTime").asText();
        String venue = getText(event, "_embedded.venues[0].name");
        String city = getText(event, "_embedded.venues[0].city.name");
        String country = getText(event, "_embedded.venues[0].country.name");
        List<Artist> artists = extractArtists(event);
        String priceInfo = extractPriceInfo(event);
        return new Event(name, date, time, venue, city, country, artists, priceInfo);
    }

    private boolean isMusicEvent(JsonNode event) {
        JsonNode classifications = event.path("classifications").get(0);
        String type = classifications.path("segment").path("name").asText("No disponible");
        return "Music".equalsIgnoreCase(type);
    }

    private List<Artist> extractArtists(JsonNode event) {
        List<Artist> artists = new ArrayList<>();
        JsonNode performers = event.path("_embedded").path("attractions");
        if (performers.isArray()) {
            for (JsonNode p : performers) {
                artists.add(new Artist(p.path("name").asText()));
            }
        }
        return artists;
    }

    private String extractPriceInfo(JsonNode event) {
        JsonNode prices = event.path("priceRanges");
        if (prices.isArray()) {
            for (JsonNode p : prices) {
                double min = p.path("min").asDouble();
                double max = p.path("max").asDouble();
                String currency = p.path("currency").asText();
                return min + " - " + max + " " + currency;
            }
        }
        return "No disponible";
    }

    private String getText(JsonNode root, String path) {
        String[] parts = path.split("\\.");
        JsonNode node = root;
        for (String part : parts) {
            if (part.matches(".+\\[\\d+]$")) {
                String field = part.substring(0, part.indexOf('['));
                int index = Integer.parseInt(part.substring(part.indexOf('[') + 1, part.indexOf(']')));
                node = node.path(field);
                if (node.isArray() && node.size() > index) {
                    node = node.get(index);
                } else return "No disponible";
            } else {
                node = node.path(part);
            }
        }
        return node.asText("No disponible");
    }
}
