package ulpgc.eventstorebuilder.port;

import jakarta.jms.Message;

public interface MessageProcessor {
    void process(Message message);
}
