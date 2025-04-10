package org.example;

public class Event {
    private long ts;
    private String ss;

    // Comunes
    private String artistName;  // Para Spotify
    private String description;

    // Ticketmaster específicos
    private String name;        // Para Ticketmaster
    private String date;
    private String time;
    private String venue;
    private String city;
    private String country;
    private String artists;     // Extraído del campo "description" en Ticketmaster
    private String priceInfo;

    // Constructores
    public Event() {}

    public Event(long ts, String ss, String name, String description) {
        this.ts = ts;
        this.ss = ss;
        this.name = name;
        this.description = description;
    }

    // Getters y Setters
    public long getTs() { return ts; }
    public void setTs(long ts) { this.ts = ts; }

    public String getSs() { return ss; }
    public void setSs(String ss) { this.ss = ss; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }  // Método getter para "name"
    public void setName(String name) { this.name = name; }  // Método setter para "name"

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getArtists() { return artists; }
    public void setArtists(String artists) { this.artists = artists; }

    public String getPriceInfo() { return priceInfo; }
    public void setPriceInfo(String priceInfo) { this.priceInfo = priceInfo; }

    @Override
    public String toString() {
        return "Event{" +
                "ts=" + ts +
                ", ss='" + ss + '\'' +
                ", artistName='" + artistName + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", venue='" + venue + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", artists='" + artists + '\'' +
                ", priceInfo='" + priceInfo + '\'' +
                '}';
    }
}
