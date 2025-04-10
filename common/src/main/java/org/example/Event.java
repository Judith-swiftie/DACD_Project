package org.example;

public class Event {
    private long ts;
    private String ss;

    private String artistName;
    private String description;

    private String name;
    private String date;
    private String time;
    private String venue;
    private String city;
    private String country;
    private String artists;
    private String priceInfo;

    public Event() {}

    public Event(long ts, String ss, String name, String description) {
        this.ts = ts;
        this.ss = ss;
        this.name = name;
        this.description = description;
    }

    public long getTs() { return ts; }

    public String getSs() { return ss; }

    public String getArtistName() { return artistName; }

    public String getDescription() { return description; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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
