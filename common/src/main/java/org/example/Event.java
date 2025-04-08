package org.example;

public class Event {
    private long ts;
    private String ss;
    private String eventName;
    private String location;
    private String description;

    public Event(long ts, String ss, String eventName, String location, String description) {
        this.ts = ts;
        this.ss = ss;
        this.eventName = eventName;
        this.location = location;
        this.description = description;
    }

    public long getTs() {
        return ts;
    }

    public String getSs() {
        return ss;
    }

    public String getEventName() {
        return eventName;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }
}
