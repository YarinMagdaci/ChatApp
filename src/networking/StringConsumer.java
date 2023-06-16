// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package networking;

/**
 * This interface represents a consumer of strings.
 */
public interface StringConsumer {

    // Consumes the provided text
    void consume(String text) throws ChatException;
}

