package org.example;

public class Main {
    public static void main(String[] args) {
        BusinessUnit businessUnit = new BusinessUnit("topicName", "pathToEventStore");
        businessUnit.start();

        // Cargar eventos históricos (si es necesario)
        businessUnit.loadHistoricalEvents();
    }
}

