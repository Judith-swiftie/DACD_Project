package org.example;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> new EventStoreBuilder("playlist").startEventStore()).start();
        new Thread(() -> new EventStoreBuilder("events").startEventStore()).start();
    }
}
