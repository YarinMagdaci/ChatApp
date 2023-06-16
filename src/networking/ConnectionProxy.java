// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package networking;

import clientside.SimpleClientGUI;
import serverside.ServerApplication;

import java.io.*;
import java.net.Socket;

/**
 * Represents a connection proxy that facilitates communication between a user and a server.
 * The class supports both server-side and client-side proxy functionality, enabling seamless communication between the two endpoints.
 * It implements the StringConsumer and StringProducer interfaces, allowing it to consume and produce string data.
 * This class implement Proxy Design pattern
 */
public class ConnectionProxy extends Thread implements StringConsumer, StringProducer {
    public static final String port = "1300";
    public static final String ip = "127.0.0.1";
    private SimpleClientGUI gui;

    // Indicates if it's a server-side proxy
    private boolean serverSideProxy = false;

    // Indicates if the connection is established
    private boolean connected;
    private StringConsumer consumer = null;

    // The socket for communication
    private Socket socket = null;

    // Input stream to receive data from the socket
    private final InputStream is;

    // Output stream to send data to the socket
    private final OutputStream os;

    // Data input stream to read structured data
    private final DataInputStream dis;

    // Data output stream to write structured data
    private final DataOutputStream dos;

    public ConnectionProxy(Socket socket, boolean serverSideProxy, SimpleClientGUI gui) throws IOException {
        setServerSideProxy(serverSideProxy);
        setGui(gui);
        setSocket(socket);
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
        this.dis = new DataInputStream(is);
        this.dos = new DataOutputStream(os);
        this.connected = this.socket.isConnected();
    }

    // Setter for the GUI object
    public void setGui(SimpleClientGUI gui) {
        this.gui = gui;
    }

    // Setter for the server-side proxy flag
    public void setServerSideProxy(boolean serverSideProxy) {
        this.serverSideProxy = serverSideProxy;
    }

    // Setter for the Socket object
    public void setSocket(Socket socket) {
        this.socket = socket;
    }


    // Disconnects from the server by closing the socket and associated streams
    public void disconnect() {
        this.removeConsumer(null);
        this.connected = false;
        if(this.socket !=null && this.socket.isConnected()) {
            try {
                // Close the socket, input&output stream, data input&output stream
                this.socket.close();
                this.is.close();
                this.os.close();
                this.dis.close();
                this.dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Returns the data input stream associated with the connection proxy
    public DataInputStream getDis() {
        return dis;
    }

    // Add a consumer to the connection proxy to receive strings
    @Override
    public void addConsumer(StringConsumer consumer) {
        this.consumer = consumer;
    }


    // Remove a consumer from the connection proxy
    @Override
    public void removeConsumer(StringConsumer consumer) {
        if (this.consumer == consumer) {
            this.consumer = null;
        }
    }


    // Receives a string as input and sends it to the connected server
    @Override
    public void consume(String text) {
        try {
            // Try to write the provided text to the data output stream
            dos.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread's method
    @Override
    public void run() {
        if (this.serverSideProxy) {
            // Running the server-side proxy
            while (this.connected) {
                try {
                    // Handle the incoming request from the user
                    String responseString = ServerApplication.handleRequest(this);

                    // If a response is generated, send it back to the user through the consumer
                    if (responseString != null) {
                        this.consumer.consume(responseString);
                    }
                } catch (IOException | ChatException e) {
                    // Disconnect from the server if an IO or ChatException occurs
                    this.disconnect();
                }
            }
        } else {
            // Running the client-side proxy
            while (this.connected) {
                try {
                    // Read the incoming message from the server
                    String msgToIntrepret = this.dis.readUTF();

                    // Skip empty messages and continue to the next iteration
                    if (msgToIntrepret.equals("")) {
                        continue;
                    }

                    // Convert the message to a Response object
                    Response resFromServer = Response.convertStringToResponse(msgToIntrepret);

                    // If a GUI object isn't null, handle the response on the client-side through the GUI
                    if (this.gui != null) {
                        this.gui.handleRequestClientSide(resFromServer);
                    }
                } catch (Exception e) {
                    // Disconnect from the server if an exception occurs
                    this.disconnect();
                }
            }
        }
    }
}
