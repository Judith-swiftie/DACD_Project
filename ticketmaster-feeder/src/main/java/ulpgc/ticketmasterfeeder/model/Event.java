package ulpgc.ticketmasterfeeder.model;

import java.util.List;

public class Event {
    private String name;
    private String date;
    private String time;
    private String venue;
    private String city;
    private String country;
    private List<Artist> artists;
    private String priceInfo;

    public Event(String name, String date, String time, String venue, String city, String country, List<Artist> artists, String priceInfo) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.city = city;
        this.country = country;
        this.artists = artists;
        this.priceInfo = priceInfo;
    }

    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getVenue() {
        return venue;
    }
    public String getCity() {
        return city;
    }
    public String getCountry() {
        return country;
    }
    public List<Artist> getArtists() {
        return artists;
    }
    public String getPriceInfo() {
        return priceInfo;
    }

    @Override
    public String toString() {
        StringBuilder artistsInfo = new StringBuilder();
        for (Artist artist : artists) {
            artistsInfo.append(artist.getName()).append(", ");
        }
        if (!artistsInfo.isEmpty()) {
            artistsInfo.setLength(artistsInfo.length() - 2);
        }
        return "Evento: " + name + "\n" +
                "Fecha: " + date + "\n" +
                "Hora: " + time + "\n" +
                "Lugar: " + venue + "\n" +
                "Ciudad: " + city + "\n" +
                "País: " + country + "\n" +
                "Artistas: " + (artistsInfo.length() > 0 ? artistsInfo : "No disponible") + "\n" +
                "Precios: " + priceInfo;
    }
}
