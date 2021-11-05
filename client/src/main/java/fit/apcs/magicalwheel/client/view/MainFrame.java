package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serial;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fit.apcs.magicalwheel.client.connection.Client;

public class MainFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 7641338013687300073L;
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());

    private static final String GAME_NAME = "Magical Wheel";

    JButton playButton;
    JTextField usernameField;

    public MainFrame() {
        setTitle(GAME_NAME);
        setOnExitEvent();
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setIconImage(new ImageIcon(getImageURL("wheel.png")).getImage());
        getContentPane().setBackground(Color.BLACK);
        addMainPanel();
        setVisible(true);
    }

    private void setOnExitEvent() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    Client.getInstance().closeConnection();
                    System.exit(0);
                } catch (IOException ignore) {
                    System.exit(1);
                }
            }
        });
    }

    private void addMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(15,0,0,0);

        mainPanel.add(centralLabel(), gbc);
        mainPanel.add(usernameField(), gbc);
        mainPanel.add(playButton(), gbc);

        this.add(mainPanel);
    }

    private JPanel usernameField() {
        final var usernamePanel = new JPanel();
        final var usernameLabel = new JLabel("Enter username: ");
        usernameField = new JTextField(10);

        usernameLabel.setForeground(Color.WHITE);
        usernamePanel.setOpaque(false);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        return usernamePanel;
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
        final var username = usernameField.getText();
        System.out.println(username);

        try {
            LOGGER.log(Level.INFO, "Button clicked");
            final var client = Client.getInstance();
            client.openConnection();
            client.sendUsername(username);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in connecting to server", ex);
        }
    }

    private JLabel centralLabel() {
        final var label = new JLabel();
        label.setText(GAME_NAME);
        label.setIcon(new ImageIcon(getImageURL("wheel.png")));
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.TOP);
        label.setForeground(Color.YELLOW);
        label.setFont(new Font("Goudy Old Style", Font.BOLD, 50));
        label.setIconTextGap(20);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private URL getImageURL(String imageName) {
        return Optional.ofNullable(getClass().getClassLoader().getResource(imageName))
                       .orElseThrow(() -> new RuntimeException("Cannot find image " + imageName));
    }

}
