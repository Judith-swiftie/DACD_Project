package ulpgc.eventstorebuilder;

import jakarta.jms.Message;

public interface MessageProcessor {
    void process(Message message);
}
