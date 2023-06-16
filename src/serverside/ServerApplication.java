// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package serverside;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import networking.*;
import org.json.JSONObject;

/**
 * The ServerApplication class serves as the main server application that handles
 * user connections, requests, and responses.
 * It utilizes a MessageBoard to manage user connections and communication.
 */
public class ServerApplication {
    private static final MessageBoard mb = new MessageBoard();
    public static void main(String[] args) {
        ServerSocket server;
        Socket socket;
        ClientDescriptor client;
        ConnectionProxy connection;
        try {
            // Create server socket with port number 1300
            server = new ServerSocket(1300);

            // Run server to listen infinitely
            while (true) {
                // Accept new user socket
                socket = server.accept();
                // Initialize connectionProxy
                // 'true' is saying to the ConnectionProxy on run method activate server-side's code
                connection = new ConnectionProxy(socket, true, null);

                // Initialize ClientDescriptor
                client = new ClientDescriptor();
                // Assigns ClientDescriptor as ConnectionProxy's consumer
                connection.addConsumer(client);
                // Assigns MessageBoard as ClientDescriptor's consumer
                client.addConsumer(mb);
                // Assigns ConnectionProxy as MessageBoard's consumer
                mb.addConsumer(connection);
                // Run in a separate thread ConnectionProxy to listen infinitely to server's responses
                connection.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Handles a request received through the given connection proxy and returns the corresponding response string
    public static String handleRequest(ConnectionProxy connectionProxy) throws IOException, ChatException {
        // Read the message from the user through the connection proxy
        String msgFromUser = connectionProxy.getDis().readUTF();

        // Convert the received message to a Request object
        Request clientsRequestInterpreted = Request.convertStringToRequest(msgFromUser);

        // Extract the necessary information from the request
        String nickName = clientsRequestInterpreted.getBody().get("nick-name").toString();
        int requestCode = clientsRequestInterpreted.getCode();

        // Create objects and variables for the response generation
        JSONObject responseBody = new JSONObject();
        Response response = null;
        String message;
        String sendTo;
        String sender;
        Set<String> keys;

        // Process the request based on the request code
        switch (requestCode) {
            // requestCode = 0, means - disconnection of a user
            case 0 -> {
                // Remove the consumer and the corresponding entry from the map
                mb.removeConsumer(connectionProxy);
                mb.getMap().remove(nickName);

                // Retrieve all names of connected users right now.
                keys = mb.getMap().keySet();

                // Prepare the response body with code 22 to update names on the client-side combobox
                // code 22 means the client-side to update names on the combobox
                responseBody.put("code", "22");
                responseBody.put("connected-users", keys);

                // Create a response with the prepared response body and status code 202
                response = new Response(responseBody, 202);
            }

            // requestCode = 1, means - connection of new user
            case 1 -> {
                // Add the user to the Map of names on server on messageboard
                mb.getMap().put(nickName, connectionProxy);
                // Retrieve all names of connected users right now.
                keys = mb.getMap().keySet();

                // Prepare the response body with code 2 to add the new name to the client-side combobox
                // code 2 means the client-side to add new name to the combobox
                responseBody.put("code", "2");
                responseBody.put("nick-name", clientsRequestInterpreted.getBody().get("nick-name"));
                responseBody.put("connected-users", keys);

                // Create a response with the prepared response body and status code 202 0
                response = new Response(responseBody, 202);
            }

            // requestCode = 2, means -  text message of a user to all users
            case 2 -> {
                // Extract the message from the request body
                message = clientsRequestInterpreted.getBody().get("message").toString();

                // Prepare the response body with code 3 to indicate a text message to all users
                // code 3 means the client-side that user wants to send text
                responseBody.put("code", "3");
                responseBody.put("sender", clientsRequestInterpreted.getBody().get("sender"));
                responseBody.put("message", message);

                // Create a response with the prepared response body and status code 203
                response = new Response(responseBody, 203);
            }

            // requestCode = 3, means - text message of a user to specific user
            case 3 -> {
                // Extract the message, sender, and recipient information from the request body
                message = clientsRequestInterpreted.getBody().get("message").toString();
                sender = clientsRequestInterpreted.getBody().get("sender").toString();
                sendTo = clientsRequestInterpreted.getBody().get("send-to").toString();

                // Prepare the response body with code 5 to indicate a private text message
                // code 5 means the client-side that user wants to send PRIVATE text-message
                responseBody.put("code", "5");
                responseBody.put("sender", clientsRequestInterpreted.getBody().get("sender"));
                responseBody.put("message", message);

                // Create a response with the prepared response body and status code 203
                response = new Response(responseBody, 203);

                // get the sendTo's connectionProxy you need to send the message
                ConnectionProxy sendToProxy = mb.getMap().get(sendTo);
                // get the sender's connectionProxy you need to send the message
                ConnectionProxy senderProxy = mb.getMap().get(sender);

                // Send the response to both the recipient and sender
                sendToProxy.consume(Response.convertResponseToString(response));
                senderProxy.consume(Response.convertResponseToString(response));

                // We manually send to the one and only user endpoint the message
                // Since the message is sent manually, return null to avoid returning a response string
                return null;
            }
            default -> {
            }
        }

        // Convert the response to a string
        String responseString = "";
        if (response != null) {
            responseString = Response.convertResponseToString(response);
        }
        return responseString;
    }
}