// Yarin Magdaci - 207702994
// Lital Kraft - 314806647

package clientside;

import org.json.JSONObject;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;
import networking.*;


/**
 * Implementation of the disconnected state
 * Implements State Design pattern
 */
public class DisconnectedState implements ConnectionState {
    @Override
    // Connect to the server
    public void connect(SimpleClientGUI clientGUI) {
        // Show popup window for server IP, port, and nickname input
        JFrame popupFrame = new JFrame("Connect to Server");
        JPanel popupPanel = new JPanel(new GridLayout(4, 2, 20, 16));
        JButton startButton = new JButton("Start");
        JButton cancelButton = new JButton("Cancel");

        JTextField serverIPField = new JTextField();
        JTextField portField = new JTextField();
        JTextField nicknameField = new JTextField();

        // Setting font and foreground for UI components
        Font font = new Font("Comic Sans MS", Font.BOLD, 16);
        serverIPField.setFont(font);
        portField.setFont(font);
        nicknameField.setFont(font);
        startButton.setFont(font);
        cancelButton.setFont(font);
        startButton.setForeground(Color.BLUE);
        cancelButton.setForeground(Color.BLUE);

        JLabel serverIPLabel = new JLabel("Server IP:");
        JLabel portLabel = new JLabel("Port:");
        JLabel nicknameLabel = new JLabel("Nickname:");

        // Setting font and foreground for serverIPLabel,portLabel,nicknameLabel
        serverIPLabel.setForeground(Color.BLUE);
        serverIPLabel.setFont(font);
        portLabel.setForeground(Color.BLUE);
        portLabel.setFont(font);
        nicknameLabel.setForeground(Color.BLUE);
        nicknameLabel.setFont(font);

        // Adding UI components to the panel
        popupPanel.add(serverIPLabel);
        popupPanel.add(serverIPField);
        popupPanel.add(portLabel);
        popupPanel.add(portField);
        popupPanel.add(nicknameLabel);
        popupPanel.add(nicknameField);
        popupPanel.add(startButton);
        popupPanel.add(cancelButton);

        // Configuring and displaying the pop-up window
        popupFrame.getContentPane().add(popupPanel);
        popupFrame.setSize(300, 200);
        popupFrame.setResizable(false);
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popupFrame.setLocationRelativeTo(clientGUI.getFrame());
        popupFrame.setVisible(true);

        // ActionListener for the "Start" button
        // Implements the Observer Design Pattern
        startButton.addActionListener(e -> {
            // Input details from the user
            String serverIP = serverIPField.getText();
            String port = portField.getText();
            String nickname = nicknameField.getText();

            // Validating required fields
            boolean emptyField = (serverIP.isEmpty()) || (port.isEmpty()) || (nickname.isEmpty());

            // Check if there is empty field or invalid port number
            if (emptyField || !port.equals(ConnectionProxy.port)) {
                JLabel errorMessageLabel;
                String error;

                // Create a custom error message
                if (!port.isEmpty() && !port.equals(ConnectionProxy.port))
                {
                    error = "Invalid port! Port should be 1300";
                }
                else
                {
                    error = "Please enter all required fields!";
                }
                summonErrorPopUp(clientGUI, error);
                // Don't proceed if any field is empty
                return;
            }

            // Attempt to the server using serverIP, port, and nickname
            try {
                // 'false' is saying to the ConnectionProxy on run method activate server-side's code
                clientGUI.setConnectionProxy(new ConnectionProxy(new Socket(serverIP, Integer.parseInt(port)), false, clientGUI));
                // Transition to Connected state
                clientGUI.setConnectionState(new ConnectedState());
                // Update button states based on the current connection state
                clientGUI.updateButtonStates();
                clientGUI.setNickName(nickname);
                clientGUI.getFrame().setTitle(nickname);

                // Code 1 says to the server - new user arrived!
                Request request = new Request(HttpMethod.PUT, new JSONObject().put("nick-name", clientGUI.getNickName()), 1);
                clientGUI.getConnectionProxy().consume(Request.convertRequestToString(request));
                clientGUI.getConnectionProxy().start();
                popupFrame.dispose();

                // Create a custom welcome dialog
                JDialog dialog = new JDialog(clientGUI.getFrame(), "Welcome " + nickname);
                dialog.setModal(true);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(300, 200);
                dialog.setLocationRelativeTo(clientGUI.getFrame());

                // Customize the dialog components
                JLabel welcomeLabel = new JLabel("Welcome " + nickname + "!");
                welcomeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
                welcomeLabel.setForeground(Color.BLUE);
                welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dialog.add(welcomeLabel, BorderLayout.CENTER);

                JButton closeButton = new JButton("Close");
                closeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
                closeButton.setForeground(Color.BLUE);

                closeButton.addActionListener(e1 -> dialog.dispose());

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(closeButton);
                dialog.add(buttonPanel, BorderLayout.SOUTH);

                // Set a custom dialog icon
                ImageIcon icon = new ImageIcon("icon.png");
                dialog.setIconImage(icon.getImage());

                // Show the custom dialog
                dialog.setVisible(true);

                // Transition to Connected state
                clientGUI.setConnectionState(new ConnectedState());
                // Update button states based on the current connection state
                clientGUI.updateButtonStates();
                popupFrame.dispose();
            } catch (IOException ex) {
                summonErrorPopUp(clientGUI, "Wrong IP!");
            }
        });

        cancelButton.addActionListener(e -> popupFrame.dispose());
    }

    @Override
    // The user already disconnected from the server
    public void disconnect(SimpleClientGUI clientGUI) { }

    @Override
    // Checks if connection can be established
    public boolean canConnect() {
        // Can connect to the server because the user disconnected
        return true;
    }

    @Override
    // Checks if disconnection can be performed
    public boolean canDisconnect() {
        // Cannot disconnect from the server because the user already disconnected
        return false;
    }

    private void summonErrorPopUp(SimpleClientGUI clientGUI, String error)
    {
        //                throw new RuntimeException(ex);
        clientGUI.setConnectionState(new DisconnectedState());
        // Update button states based on the current connection state
        clientGUI.updateButtonStates();
        Font font1 = new Font("Comic Sans MS", Font.BOLD, 14);
        Color color = Color.RED;
        JLabel errorMessageLabel;

        // Create a custom error message
        errorMessageLabel = new JLabel(error);

        // Set desired font and foreground
        errorMessageLabel.setFont(font1);
        errorMessageLabel.setForeground(color);

        // Show error message dialog with the custom label
        JOptionPane.showMessageDialog(clientGUI.getFrame(),
                errorMessageLabel,
                "Invalid Information",
                JOptionPane.ERROR_MESSAGE);

        // Get the default close button and set the custom font
        UIManager.put("OptionPane.buttonFont", font1);

    }
}
