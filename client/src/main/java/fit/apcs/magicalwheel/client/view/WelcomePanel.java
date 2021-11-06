package fit.apcs.magicalwheel.client.view;

import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.Serial;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.model.Player;

public class WelcomePanel extends JPanel {
    
    private static final Logger LOGGER = Logger.getLogger(WelcomePanel.class.getName());

    @Serial
    private static final long serialVersionUID = 274489371310292356L;

    private JButton playButton;
    private JTextField usernameField;
    private JLabel message;

    public WelcomePanel() {
        // for display purpose only (to guarantee that this text always takes 1 line)
        message = new JLabel(" ");
        message.setForeground(Color.WHITE);

        setOpaque(false);
        setLayout(new GridBagLayout());

        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(15,0,0,0);

        add(centralLabel(), gbc);
        add(usernameField(), gbc);
        add(playButton(), gbc);
        add(message, gbc);
    }

    private JPanel usernameField() {
        final var usernamePanel = new JPanel();
        final var usernameLabel = new JLabel("Enter username: ");

        handleUsernameField();

        usernameLabel.setForeground(Color.WHITE);
        usernamePanel.setOpaque(false);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        return usernamePanel;
    }

    private void handleUsernameField() {
        usernameField = new JTextField(10);
        usernameField.setDocument(new JTextFieldLimit(10));
        usernameField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                resetButtonAndMessage();   
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                resetButtonAndMessage();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                resetButtonAndMessage();
            }

            public void resetButtonAndMessage() {
                message.setText(" ");
                playButton.setEnabled(true);
            }

        });
    }

    private JButton playButton() {
        playButton = new JButton();
        playButton.addActionListener(event -> onPlayButtonClickListener());
        playButton.setText("PLAY");
        playButton.setHorizontalAlignment(SwingConstants.CENTER);
        playButton.setVerticalAlignment(SwingConstants.CENTER);
        return playButton;
    }

    private void onPlayButtonClickListener() {
        playButton.setEnabled(false);
        final var username = usernameField.getText();
        if (isUsernameVerified(username)) {
            LOGGER.log(Level.INFO, "Button clicked");
            final var client = Client.getInstance();
            client.openConnection(unused -> client.sendUsername(username, this));
        }
    }

    private boolean isUsernameVerified(String username) {
        final var pattern = "^[a-zA-Z0-9_]+$";
        if (!Pattern.matches(pattern, username)) {
            message.setText("Username must be composed by 'a'..'z', 'A'..'Z', '0'..'9', '_'");
            return false;
        }
        return true;
    }

    public void setMessage(String text) {
        message.setText(text);
    }

    private JLabel centralLabel() {
        final var label = new JLabel();
        label.setText(ResourceUtil.GAME_NAME);
        label.setIcon(new ImageIcon(ResourceUtil.getImageURL("wheel.png")));
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.TOP);
        label.setForeground(Color.YELLOW);
        label.setFont(new Font("Goudy Old Style", Font.BOLD, 50));
        label.setIconTextGap(20);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    public void joinWaitingRoom(int maxNumPlayers, ArrayList<Player> listPlayers) {
        // TODO: switch to waiting room
    }

}
