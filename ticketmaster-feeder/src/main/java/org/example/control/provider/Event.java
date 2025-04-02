package org.example.control.provider;

public class Event {
    private String name;
    private String date;
    private String time;
    private String venue;
    private String city;
    private String country;
    private String artists;
    private String priceInfo;

    public Event(String name, String date, String time, String venue, String city, String country, String artists, String priceInfo) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.city = city;
        this.country = country;
        this.artists = artists;
        this.priceInfo = priceInfo;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getVenue() { return venue; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getArtists() { return artists; }
    public String getPriceInfo() { return priceInfo; }

    public void printDetails() {
        System.out.println("🎶 Evento: " + name);
        System.out.println("📅 Fecha: " + date);
        System.out.println("⏰ Hora: " + time);
        System.out.println("📍 Lugar: " + venue);
        System.out.println("🏙️ Ciudad: " + city);
        System.out.println("🌍 País: " + country);
        System.out.println("🎤 Artistas: " + (artists.isEmpty() ? "No disponible" : artists));
        System.out.println("💰 Precios: " + priceInfo);
        System.out.println("=================================");
    }
}
