// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package clientside;

/**
 * Interface representing the connection state - connected or disconnect.
 * Implements State Design pattern.
 */
public interface ConnectionState {
    // Connect to the server
    void connect(SimpleClientGUI clientGUI);

    // Disconnect from the server
    void disconnect(SimpleClientGUI clientGUI);

    // Check if connection can be established
    boolean canConnect();

    // Checks if disconnection can be performed
    boolean canDisconnect();
}