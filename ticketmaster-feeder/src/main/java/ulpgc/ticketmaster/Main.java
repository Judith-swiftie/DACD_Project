package ulpgc.ticketmaster;

import ulpgc.ticketmaster.control.ArtistFileExporter;
import ulpgc.ticketmaster.control.Controller;
import ulpgc.ticketmaster.model.Event;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        System.out.println("Iniciando obtención y envío de eventos al broker...");
        controller.fetchAndSendEvents();
        List<Event> events = controller.getEventStore().getAllEvents();
        ArtistFileExporter.exportArtistsFromEvents(events);
    }
}
