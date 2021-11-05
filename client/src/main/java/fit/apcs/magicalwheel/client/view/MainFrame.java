package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.io.Serial;
import java.net.URL;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class MainFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 7641338013687300073L;

    private static final String GAME_NAME = "Magical Wheel";


    public MainFrame() {
        setTitle(GAME_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setIconImage(new ImageIcon(getImageURL("wheel.png")).getImage());
        getContentPane().setBackground(Color.BLACK);
        addMainPanel();
        setVisible(true);
    }

    private void addMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10,0,0,0);

        mainPanel.add(centralLabel(), gbc);
        mainPanel.add(playButton(), gbc);

        this.add(mainPanel);
    }

    private JButton playButton() {
        final var playButton = new JButton();
        playButton.addActionListener(event -> onPlayButtonClickListener(playButton));
        playButton.setText("PLAY");
        playButton.setHorizontalAlignment(SwingConstants.CENTER);
        playButton.setVerticalAlignment(SwingConstants.CENTER);
        return playButton;
    }

    private void onPlayButtonClickListener(JButton playButton) {
        System.out.println("register field");
        playButton.setEnabled(false);
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
