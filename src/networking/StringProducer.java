// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package networking;

/**
 * This interface represents a StringProducer, which is responsible for managing a list of StringConsumers.
 */
public interface StringProducer {
    // Add a StringConsumer to the list of consumers
    void addConsumer(StringConsumer consumer);


    // Remove a StringConsumer from the list of consumers
    void removeConsumer(StringConsumer consumer);
}
