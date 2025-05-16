package ulpgc.eventstorebuilder;

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
            EventStoreBuilder builder = new EventStoreBuilder(topicName, processor);
            builder.startEventStore();
        }).start();
    }
}
