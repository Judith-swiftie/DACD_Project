package org.example;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
            JsonEventStore store = new FileJsonEventStore("playlist");
            new EventStoreBuilder("playlist", store).startEventStore();
        }).start();
        new Thread(() -> {
            JsonEventStore store = new FileJsonEventStore("events");
            new EventStoreBuilder("events", store).startEventStore();
        }).start();
    }
}
