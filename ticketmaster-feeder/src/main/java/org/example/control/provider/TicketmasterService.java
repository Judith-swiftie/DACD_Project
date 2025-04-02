package org.example.control.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class TicketmasterService implements ServiceStore {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY + "&countryCode=ES";
    private static final int TOTAL_PAGES = 20;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Event> fetchMusicEvents() {
        List<Event> eventsList = new ArrayList<>();

        for (int page = 0; page < TOTAL_PAGES; page++) {
            String url = BASE_URL + "&page=" + page;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 429) {
                    System.out.println("⚠️  Demasiadas solicitudes. Esperando 60 segundos antes de reintentar...");
                    Thread.sleep(60000);
                    page--;
                    continue;
                }

                if (response.statusCode() == 200) {
                    JsonNode rootNode = objectMapper.readTree(response.body());
                    JsonNode eventsNode = rootNode.path("_embedded").path("events");

                    if (eventsNode.isArray()) {
                        for (JsonNode event : eventsNode) {
                            Event musicEvent = parseEvent(event);
                            if (musicEvent != null) {
                                eventsList.add(musicEvent);
                            }
                        }
                    }
                } else {
                    System.out.println("❌ Error en la API: " + response.statusCode());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return eventsList;
    }

    protected Event parseEvent(JsonNode event) {
        String name = event.path("name").asText();
        String date = event.path("dates").path("start").path("localDate").asText();
        String time = event.path("dates").path("start").path("localTime").asText();
        String venue = event.path("_embedded").path("venues").get(0).path("name").asText();
        String city = event.path("_embedded").path("venues").get(0).path("city").path("name").asText();
        String country = event.path("_embedded").path("venues").get(0).path("country").path("name").asText();

        JsonNode classifications = event.path("classifications").get(0);
        String type = classifications.path("segment").path("name").asText("No disponible");

        if (!"Music".equalsIgnoreCase(type)) {
            return null;
        }

        JsonNode performersNode = event.path("_embedded").path("attractions");
        StringBuilder artists = new StringBuilder();
        if (performersNode.isArray()) {
            for (JsonNode performer : performersNode) {
                artists.append(performer.path("name").asText()).append(", ");
            }
            if (!artists.isEmpty()) {
                artists.setLength(artists.length() - 2);
            }
        }

        JsonNode priceRanges = event.path("priceRanges");
        String priceInfo = "No disponible";
        if (priceRanges.isArray()) {
            for (JsonNode priceRange : priceRanges) {
                double minPrice = priceRange.path("min").asDouble();
                double maxPrice = priceRange.path("max").asDouble();
                String currency = priceRange.path("currency").asText();
                priceInfo = minPrice + " - " + maxPrice + " " + currency;
            }
        }

        return new Event(name, date, time, venue, city, country, artists.toString(), priceInfo);
    }
}
