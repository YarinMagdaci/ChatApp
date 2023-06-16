// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package networking;

/**
 * Custom exception class for handling chat-related exceptions.
 * This class extends the base Exception class.
 */
public class ChatException extends Exception {
    // Constructs a new ChatException with the specified detail message
    public ChatException(String message) {
        super(message);
    }

    // Constructs a new ChatException with the specified detail message and cause
    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }
}

