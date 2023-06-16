// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package serverside;
import networking.*;
/**
 * This class represents a client descriptor that can both consume and produce strings.
 * It implements the 'StringProducer' and 'StringConsumer' interfaces.
 */
public class ClientDescriptor implements StringProducer, StringConsumer {
    private StringConsumer consumer;

    // Consumes the provided text by passing it to the associated consumer
    @Override
    public void consume(String text) throws ChatException {
        this.consumer.consume(text);
    }

    // Adds a consumer to the client descriptor
    @Override
    public void addConsumer(StringConsumer consumer) {
        this.consumer = consumer;
    }

    // Removes the consumer from the client descriptor
    @Override
    public void removeConsumer(StringConsumer consumer) {
        this.consumer = null;
    }
}

