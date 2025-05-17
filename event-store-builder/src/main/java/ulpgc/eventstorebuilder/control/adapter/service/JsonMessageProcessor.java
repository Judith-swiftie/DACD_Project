package ulpgc.eventstorebuilder.control.adapter.service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import ulpgc.eventstorebuilder.control.port.JsonEventStore;
import ulpgc.eventstorebuilder.control.port.MessageProcessor;

public class JsonMessageProcessor implements MessageProcessor {
    private final JsonEventStore jsonEventStore;

    public JsonMessageProcessor(JsonEventStore jsonEventStore) {
        this.jsonEventStore = jsonEventStore;
    }

    @Override
    public void process(Message message) {
        if (message instanceof TextMessage textMessage) {
            try {
                String json = textMessage.getText();
                System.out.println("- Mensaje recibido: " + json);
                jsonEventStore.saveJson(json);
                System.out.println("- Evento JSON almacenado.");
            } catch (JMSException e) {
                System.err.println("- Error al obtener el texto del mensaje: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("- Error procesando el mensaje: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("- Mensaje recibido no es de tipo TextMessage.");
        }
    }
}
