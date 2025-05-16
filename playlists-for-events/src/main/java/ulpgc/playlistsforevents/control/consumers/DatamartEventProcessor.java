package ulpgc.playlistsforevents.control.consumers;

import ulpgc.playlistsforevents.control.store.Datamart;
import ulpgc.playlistsforevents.model.Event;

public class DatamartEventProcessor implements EventProcessor {
    private final Datamart datamart;

    public DatamartEventProcessor(Datamart datamart) {
        this.datamart = datamart;
    }

    @Override
    public void process(String json) {
        Event event = Event.fromJson(json);
        datamart.addEvent(event);
    }
}
