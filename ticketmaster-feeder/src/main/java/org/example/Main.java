package org.example;

import org.example.control.Controller;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();

        System.out.println("🎫 Iniciando obtención y envío de eventos al broker...");
        controller.fetchAndSendEvents();
    }
}
