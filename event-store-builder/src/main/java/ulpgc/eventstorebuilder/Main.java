package ulpgc.eventstorebuilder;

import ulpgc.eventstorebuilder.control.Controller;
import ulpgc.eventstorebuilder.control.JsonMessageProcessor;
import ulpgc.eventstorebuilder.port.FileWriter;
import ulpgc.eventstorebuilder.port.JsonEventPathBuilder;
import ulpgc.eventstorebuilder.port.JsonEventStore;
import ulpgc.eventstorebuilder.port.MessageProcessor;
import ulpgc.eventstorebuilder.store.DefaultFileWriter;
import ulpgc.eventstorebuilder.store.DefaultJsonEventPathBuilder;
import ulpgc.eventstorebuilder.store.FileJsonEventStore;

public class Main {
    public static void main(String[] args) {
        startEventStoreInThread("playlist");
        startEventStoreInThread("events");
    }

    private static void startEventStoreInThread(String topicName) {
        new Thread(() -> {
            JsonEventPathBuilder pathBuilder = new DefaultJsonEventPathBuilder();
            FileWriter fileWriter = new DefaultFileWriter();
            JsonEventStore store = new FileJsonEventStore(topicName, pathBuilder, fileWriter);
            MessageProcessor processor = new JsonMessageProcessor(store);
            Controller builder = new Controller(topicName, processor);
            builder.startEventStore();
        }).start();
    }
}
