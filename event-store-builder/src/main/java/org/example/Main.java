package org.example;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> new EventStoreBuilder("playlist.SpotifyEvents").startEventStore()).start();
        new Thread(() -> new EventStoreBuilder("concert.TicketmasterEvents").startEventStore()).start();
    }
}
