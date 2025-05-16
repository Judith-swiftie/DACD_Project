package ulpgc.playlistsforevents.control.consumers;

public interface EventProcessor {
    void process(String json);
}
