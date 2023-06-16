// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package clientside;

import networking.HttpMethod;
import networking.Request;
import org.json.JSONObject;

/**
 * Implementation of the connected state.
 * Implements State Design pattern.
 */
public class ConnectedState implements ConnectionState {
    @Override
    // The user already connected to the server, no action needed
    public void connect(SimpleClientGUI clientGUI) { }

    @Override
    // Disconnect from the server
    public void disconnect(SimpleClientGUI clientGUI) {
        // Update state - implement state Design Pattern
        clientGUI.setConnectionState(new DisconnectedState());
        // Update the buttons enabling using the state Design Pattern
        clientGUI.updateButtonStates();
        // once the user disconnects, we need to 'notify' the server to 'notify' all other clients comboboxes nicknames available
        // therefore we send a message to the server.
        Request request = new Request(HttpMethod.DELETE, new JSONObject().put("nick-name", clientGUI.getNickName()), 0);
        clientGUI.getConnectionProxy().consume(Request.convertRequestToString(request));
        // disconnect the proxy itself and release resources.
        clientGUI.getConnectionProxy().disconnect();
    }

    @Override
    // Checks if connection can be established
    public boolean canConnect() {
        // Cannot connect to the server because the user already connected to the server
        return false;
    }

    @Override
    // Checks if disconnection can be performed
    public boolean canDisconnect() {
        // Can disconnect from the server because the user already connected to the server
        return true;
    }
}
