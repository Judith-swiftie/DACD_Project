package ulpgc.ticketmasterfeeder.control;

import ulpgc.ticketmasterfeeder.model.Event;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtistFileExporter {

    private static final String OUTPUT_FILE = "artists.txt";

    public static void exportArtistsFromEvents(List<Event> events) {
        Set<String> uniqueArtists = new HashSet<>();
        for (Event event : events) {
            event.getArtists().forEach(artist -> uniqueArtists.add(artist.getName()));
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            for (String artist : uniqueArtists) {
                writer.write(artist);
                writer.newLine();
            }
            System.out.println("Archivo de artistas creado: " + OUTPUT_FILE);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo de artistas: " + e.getMessage());
        }
    }
}

