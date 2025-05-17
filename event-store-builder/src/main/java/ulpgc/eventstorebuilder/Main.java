package ulpgc.eventstorebuilder;

import ulpgc.eventstorebuilder.control.adapter.service.Controller;
import ulpgc.eventstorebuilder.control.adapter.service.JsonMessageProcessor;
import ulpgc.eventstorebuilder.control.port.FileWriter;
import ulpgc.eventstorebuilder.control.port.JsonEventPathBuilder;
import ulpgc.eventstorebuilder.control.port.JsonEventStore;
import ulpgc.eventstorebuilder.control.port.MessageProcessor;
import ulpgc.eventstorebuilder.control.adapter.store.DefaultFileWriter;
import ulpgc.eventstorebuilder.control.adapter.store.DefaultJsonEventPathBuilder;
import ulpgc.eventstorebuilder.control.adapter.store.FileJsonEventStore;

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
