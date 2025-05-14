package ulpgc.ticketmaster.control.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ulpgc.ticketmaster.model.Artist;
import ulpgc.ticketmaster.model.Event;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterEventProvider implements MusicalEventProvider {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY + "&countryCode=ES";
    private static final int TOTAL_PAGES = 20;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Event> fetchMusicEvents() {
        List<Event> eventsList = new ArrayList<>();
        for (int page = 0; page < TOTAL_PAGES; page++) {
            String url = buildPageUrl(page);
            try {
                String body = fetchPage(url);
                if (body == null) {
                    page--;
                    continue;
                }
                List<Event> pageEvents = parseEventsFromBody(body);
                eventsList.addAll(pageEvents);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return eventsList;
    }
    private String buildPageUrl(int page) {
        return BASE_URL + "&page=" + page;
    }

    private String fetchPage(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 429) {
            System.out.println("Demasiadas solicitudes. Esperando 60 segundos antes de reintentar...");
            Thread.sleep(60000);
            return null;
        } else if (response.statusCode() != 200) {
            System.out.println("---Error en la API: " + response.statusCode());
            return null;
        }
        return response.body();
    }

    private List<Event> parseEventsFromBody(String body) throws Exception {
        List<Event> eventList = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(body);
        JsonNode eventsNode = rootNode.path("_embedded").path("events");
        if (eventsNode.isArray()) {
            for (JsonNode event : eventsNode) {
                Event musicEvent = parseEvent(event);
                if (musicEvent != null) {
                    eventList.add(musicEvent);
                }
            }
        }
        return eventList;
    }

    protected Event parseEvent(JsonNode event) {
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
        JsonNode performersNode = event.path("_embedded").path("attractions");
        if (performersNode.isArray()) {
            for (JsonNode performer : performersNode) {
                String artistName = performer.path("name").asText();
                artists.add(new Artist(artistName));
            }
        }
        return artists;
    }

    private String extractPriceInfo(JsonNode event) {
        JsonNode priceRanges = event.path("priceRanges");
        if (priceRanges.isArray()) {
            for (JsonNode priceRange : priceRanges) {
                double minPrice = priceRange.path("min").asDouble();
                double maxPrice = priceRange.path("max").asDouble();
                String currency = priceRange.path("currency").asText();
                return minPrice + " - " + maxPrice + " " + currency;
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
                } else {
                    return "No disponible";
                }
            } else {
                node = node.path(part);
            }
        }
        return node.asText("No disponible");
    }

}
