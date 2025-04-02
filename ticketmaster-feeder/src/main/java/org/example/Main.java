package org.example;

import org.example.control.provider.Event;
import org.example.control.provider.TicketmasterService;
import org.example.control.store.SqliteEventStore;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        TicketmasterService service = new TicketmasterService();
        SqliteEventStore dbManager = new SqliteEventStore();
        Timer timer = new Timer();

        System.out.println("🎫 Iniciando consulta periódica de eventos...");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<Event> events = service.fetchMusicEvents();
                dbManager.saveEvents(events);
                System.out.println("✅ Base de datos actualizada con nuevos eventos.");
            }
        }, 0, 86400000);
    }
}
