package ulpgc.ticketmasterfeeder.control.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TicketMasterClient {
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + API_KEY + "&countryCode=ES";
    private final HttpClient client;

    public TicketMasterClient(HttpClient client) {
        this.client = client;
    }

    public String fetchPage(int page) throws Exception {
        String url = BASE_URL + "&page=" + page;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 429) {
            System.out.println("Demasiadas solicitudes. Esperando 60s...");
            Thread.sleep(60000);
            return null;
        } else if (response.statusCode() != 200) {
            System.out.println("-Error en la API: " + response.statusCode());
            return null;
        }
        return response.body();
    }
}
