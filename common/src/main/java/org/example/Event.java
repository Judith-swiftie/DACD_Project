package org.example;

import java.time.Instant;
import java.util.Map;

public class Event {
    private long timestamp;
    private String source;
    private Map<String, Object> data;

    public Event(long timestamp, String source, Map<String, Object> data) {
        this.timestamp = timestamp;
        this.source = source;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Instant getTs() {
        return Instant.ofEpochMilli(timestamp);
    }
}
