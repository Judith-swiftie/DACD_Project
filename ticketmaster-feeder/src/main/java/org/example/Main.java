package org.example;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TicketmasterService service = new TicketmasterService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("🎫 Bienvenido a Ticketmaster España 🎫");
        System.out.println("📡 Buscando eventos de música...");

        List<Event> events = service.fetchMusicEvents();

        if (events.isEmpty()) {
            System.out.println("❌ No se encontraron conciertos.");
        } else {
            System.out.println("✅ Se encontraron " + events.size() + " conciertos:");
            for (int i = 0; i < events.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + events.get(i).getName());
            }

            System.out.print("\n🔎 Introduce el número del evento para ver detalles (0 para salir): ");
            int choice = scanner.nextInt();

            while (choice != 0) {
                if (choice > 0 && choice <= events.size()) {
                    events.get(choice - 1).printDetails();
                } else {
                    System.out.println("❌ Opción no válida.");
                }
                System.out.print("\n🔎 Introduce otro número (0 para salir): ");
                choice = scanner.nextInt();
            }
        }

        System.out.println("👋 ¡Gracias por usar TicketmasterAPI!");
        scanner.close();
    }
}
