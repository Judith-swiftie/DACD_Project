package org.example;

import org.example.api.PlaylistService;
import org.example.datamart.InMemoryDatamart;
import org.example.integration.SpotifyValidator;
import org.example.integration.TicketmasterReader;
import org.example.service.EventProcessor;
import org.example.service.PlaylistGenerator;

import org.example.spotify.SpotifyService;

public class Main {
    public static void main(String[] args) {
        SpotifyService spotifyService = new SpotifyService(); // Debe estar implementado
        TicketmasterReader reader = new TicketmasterReader();
        SpotifyValidator validator = new SpotifyValidator(spotifyService);
        EventProcessor processor = new EventProcessor(reader, validator);
        PlaylistGenerator generator = new PlaylistGenerator(spotifyService);
        InMemoryDatamart datamart = new InMemoryDatamart();

        PlaylistService service = new PlaylistService(processor, generator, datamart);
        service.runCLI();
    }
}
