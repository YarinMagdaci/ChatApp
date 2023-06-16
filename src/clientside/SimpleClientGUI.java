// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package clientside;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import networking.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The SimpleClientGUI class represents a simple graphical user interface for a client-side application.
 * SimpleClientGUI developed using swing - this class implement Composite Design Pattern
 */
public class SimpleClientGUI {
    private String nickName;

    // Manages the connection with the server.
    private ConnectionProxy connectionProxy;

    // Represents the main frame of the GUI.
    private final JFrame frame;

    // Allows the user to input text.
    private final JTextField textField;
    private final JButton btSend;
    private final JPanel panelSouth, panelCenter, panelNorth;
    private final JButton btConnect, btDisconnect;
    private final JTextArea chatTextArea;
    private final JScrollPane chatScrollPane;

    // Tracks the current state of the connection.
    private ConnectionState connectionState;

    // Create the combo box and the "To" label
    static JComboBox<String> comboBox;
    JLabel toLabel;

    public SimpleClientGUI() {
        // Initialize the JFrame
        frame = new JFrame();

        // Initialize the JTextField for user input
        textField = new JTextField(20);

        // Define the font for various GUI components
        Font font = new Font("Comic Sans MS", Font.BOLD, 16);

        // Initialize the Send button
        btSend = new JButton("Send");
        btSend.setFont(font);
        btSend.setForeground(Color.BLUE);
        btSend.setEnabled(false);

        // Initialize the Connect and Disconnect buttons
        btConnect = new JButton("Connect");
        btConnect.setFont(font);
        btConnect.setForeground(new Color(0, 210, 0));

        btDisconnect = new JButton("Disconnect");
        btDisconnect.setFont(font);
        btDisconnect.setForeground(Color.RED);

        // Set the preferred size and font of the JTextField for user input
        textField.setPreferredSize(new Dimension(200, 32));
        textField.setFont(font);

        // Initialize the panels for organizing the GUI components
        panelSouth = new JPanel();
        panelCenter = new JPanel();
        panelNorth = new JPanel();

        // Initialize the JTextArea for displaying chat messages
        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);

        // Initialize the JScrollPane for enabling scrolling in the chatTextArea
        chatScrollPane = new JScrollPane(chatTextArea);

        // Initialize the JComboBox for selecting a recipient
        comboBox = new JComboBox<>();
        comboBox.setEnabled(false);
        comboBox.setFont(font);
        comboBox.setForeground(Color.BLUE);

        // Initialize the JLabel for the "To" label in the combo box
        toLabel = new JLabel("To:");
        toLabel.setFont(font);
        toLabel.setForeground(Color.BLUE);

        // Set the initial connection state as DisconnectedState
        connectionState = new DisconnectedState();
    }


    // Initializes and configures the GUI components for the client application
    public void start() {
        frame.setLayout(new BorderLayout());

        // Set the background color of the panelNorth
        panelNorth.setBackground(new Color(51, 153, 255));

        // Add the Connect and Disconnect buttons to the panelNorth
        panelNorth.add(btConnect);
        panelNorth.add(btDisconnect);

        // Enable or disable the Connect and Disconnect buttons based on the connection state
        btConnect.setEnabled(connectionState.canConnect());
        btDisconnect.setEnabled(connectionState.canDisconnect());

        panelSouth.setLayout(new GridLayout(2, 1));

        // Create the topPanel for the "To" label and comboBox
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Set the background color of the topPanel
        topPanel.setBackground(new Color(51, 153, 255));

        // Add the "To" label and comboBox to the topPanel
        topPanel.add(toLabel);
        topPanel.add(comboBox);

        // Create the bottomPanel for the text field and Send button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Set the background color of the bottomPanel
        bottomPanel.setBackground(new Color(51, 153, 255));

        // Add the text field and Send button to the bottomPanel
        bottomPanel.add(textField);
        bottomPanel.add(btSend);

        // Add the topPanel and bottomPanel to the panelSouth
        panelSouth.add(topPanel);
        panelSouth.add(bottomPanel);

        // Set the preferred size of the panelSouth
        panelSouth.setPreferredSize(new Dimension(90, 82));

        // Initialize the comboBox
        comboBoxInitialization();

        // Set the preferred size of the comboBox
        comboBox.setPreferredSize(new Dimension(150, comboBox.getPreferredSize().height));

        // Set the background color of the panelCenter
        panelCenter.setBackground(Color.lightGray);

        // Set the layout of the panelCenter
        panelCenter.setLayout(new BorderLayout());

        // Add the chatScrollPane to the panelCenter
        panelCenter.add(chatScrollPane, BorderLayout.CENTER);

        // Add the panelNorth, panelCenter, and panelSouth to the frame
        frame.add(panelNorth, BorderLayout.NORTH);
        frame.add(panelCenter, BorderLayout.CENTER);
        frame.add(panelSouth, BorderLayout.SOUTH);

        // Set the size of the frame
        frame.setSize(800, 500);

        // Set the default close operation for the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the frame as visible
        frame.setVisible(true);

        // Add an ActionListener to the btSend button
        // Implements Observer Design Pattern
        btSend.addActionListener(e -> {
            String message = textField.getText();
            String sendTo = (comboBox.getSelectedItem() != null) ? comboBox.getSelectedItem().toString() : null;

            if (sendTo == null) {
                // Handle the case when no recipient is selected
                return;
            }

            // Command Design Pattern
            Command sendMessageCommand = new SendMessageCommand(message, sendTo, nickName, getConnectionProxy());
            sendMessageCommand.execute(this);

            textField.setText("");
        });


        // Add an ActionListener to the btConnect button
        // Implements Observer Design Pattern
        btConnect.addActionListener(e -> {
            // Connect to the server - call to connect() method of the connectionState
            connectionState.connect(SimpleClientGUI.this);
        });

        // Add an ActionListener to the btDisconnect button
        // Implements the Observer Design Pattern
        btDisconnect.addActionListener(e -> {
            // Disconnected from the server - call the disconnect() method of the connectionState
            connectionState.disconnect(SimpleClientGUI.this);
        });

        // Add a WindowAdapter to handle the windowClosing event of the frame
        // Implements the Observer Design Pattern
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Disconnected from the server - call the disconnect() method of the connectionState
                connectionState.disconnect(SimpleClientGUI.this);
            }
        });
    }

    // Updates the states of the buttons based on the current connection state
    public void updateButtonStates() {
        btConnect.setEnabled(connectionState.canConnect());
        btDisconnect.setEnabled(connectionState.canDisconnect());
        comboBox.setEnabled(connectionState.canDisconnect());
        btSend.setEnabled(connectionState.canDisconnect());
    }

    // Getters for the buttons - implement the State Design Pattern
    public JFrame getFrame() {
        return frame;
    }

    public ConnectionProxy getConnectionProxy() {
        return connectionProxy;
    }

    public String getNickName() {
        return this.nickName;
    }

    // Setters for the buttons
    public void setConnectionState(ConnectionState state) {
        connectionState = state;
    }

    public void setConnectionProxy(ConnectionProxy connectionProxy) {
        this.connectionProxy = connectionProxy;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    // Initializes the comboBox by adding the "Everyone" option.
    private void comboBoxInitialization() {
        comboBox.addItem("Everyone");
    }

    // Updates the comboBox based on the set of connected users
    // Clears the existing users in the comboBox and adds the "Everyone" option as the first item
    private void updateComboBox(Set<String> connectedUsers) {
        // Clear existing items
        comboBox.removeAllItems();
        // set "Everyone" first
        comboBox.addItem("Everyone");
        for (String connectedUser : connectedUsers) {
            comboBox.addItem(connectedUser);
        }
    }

    // Handles the client-side response received from a server request
    // This function processes the response based on the status code and performs
    //different actions accordingly
    public void handleRequestClientSide(Response response) {
        // Get the status code from the response
        int statusCode = response.getStatusCode();

        // Handle different status codes
        switch (statusCode) {
            // statusCode = 202, means: connection of new user
            case 202 -> {
                // Extract the "connected-users" array from the response body
                JSONArray jsonArray = response.getBody().getJSONArray("connected-users");

                // Create a set to store connected users (excluding the current user)
                Set<String> connectedUsers = new HashSet<>();

                // Iterate over the array and add each user to the set
                for (int i = 0; i < jsonArray.length(); i++) {
                    connectedUsers.add(jsonArray.getString(i));
                }

                // Remove the current user from the set
                connectedUsers.remove(this.nickName);

                // Update the combo box with the updated set of connected users
                this.updateComboBox(connectedUsers);
            }
            // statusCode = 203, means: sending text message of a user to all users
            // Or, sending text message of a user to specific user
            case 203 -> {
                // Extract sender, message, and code from the response body
                String sender = response.getBody().get("sender").toString();
                String message = response.getBody().get("message").toString();
                String code = response.getBody().get("code").toString();
                int isPrivate;

                // Determine if the message is private based on the
                // code = 5 means that the message sent to specific user
                if (code.equals("5"))
                    isPrivate = 1;
                else
                    isPrivate = 0;

                // Build a string representation of the message to display
                StringBuilder sb = new StringBuilder();

                // Append the sender's name or "You" if it's the current user
                if (sender.equals(this.nickName))
                    sb.append("You");
                else
                    sb.append(sender);

                // Append "(private)" if the message sent to specific user, otherwise append a colon
                if (isPrivate == 1)
                    sb.append(" (private): ");
                else
                    sb.append(": ");

                // Append the message content
                sb.append(message);
                sb.append("\n");

                // Append the constructed message to the chat text area
                this.chatTextArea.append(sb.toString());

                // Set the font for the chat text area
                Font font = new Font("Comic Sans MS", Font.BOLD, 16);
                chatTextArea.setFont(font);
            }
            default -> {
            }
            // Do nothing for other status codes
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleClientGUI clientGUI = new SimpleClientGUI();
            clientGUI.start();
        });
    }
}

