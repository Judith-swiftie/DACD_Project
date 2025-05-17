package ulpgc.eventstorebuilder.control.port;

import jakarta.jms.Message;

public interface MessageProcessor {
    void process(Message message);
}
