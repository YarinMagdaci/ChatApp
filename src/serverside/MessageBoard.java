// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package serverside;

import java.util.*;

import networking.*;

/**
 * MessageBoard's class's main purpose is to hold references of all users are logged in to the server
 * And send them a message in case any user [which is connected to the server] sent a message in the
 * global/local chat.
 */
public class MessageBoard implements StringConsumer, StringProducer {
    // Map to store connection proxies
    private final Map<String, ConnectionProxy> map = new HashMap<>();

    // List to store string consumers
    private final List<StringConsumer> consumers = new ArrayList<>();

    // Retrieve the map of connection proxies associated with the message board
    public Map<String, ConnectionProxy> getMap() {
        return map;
    }

    // Add a string consumer to the message board's list of consumers
    @Override
    public void addConsumer(StringConsumer consumer) {
        consumers.add(consumer);
    }


    // Remove a string consumer from the message board's list of consumers
    @Override
    public void removeConsumer(StringConsumer consumer) {
        consumers.remove(consumer);
    }


    // Implementation of Iterator design pattern
    // Consume a string by forwarding it to all the registered string consumers.
    // This method utilizes an iterator to iterate over the collection of consumers and calls the `consume` method on each consumer.
    @Override
    public void consume(String text) {
        Iterator<StringConsumer> iterator = consumers.iterator();
        while (iterator.hasNext()) {
            StringConsumer stringConsumer = iterator.next();
            try {
                stringConsumer.consume(text);
            } catch (ChatException e) {
                e.printStackTrace();
            }
        }
    }
}

