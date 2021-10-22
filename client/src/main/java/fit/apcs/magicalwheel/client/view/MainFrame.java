package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.Font;
import java.io.Serial;
import java.net.URL;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
        addCentralLabel();
        setVisible(true);
    }

    private void addCentralLabel() {
        final var label = new JLabel();
        label.setText(GAME_NAME);
        label.setIcon(new ImageIcon(getImageURL("wheel.png")));
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.TOP);
        label.setForeground(Color.CYAN);
        label.setFont(new Font("Arial", Font.BOLD, 50));
        label.setIconTextGap(20);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        add(label);
    }

    private URL getImageURL(String imageName) {
        return Optional.ofNullable(getClass().getClassLoader().getResource(imageName))
                       .orElseThrow(() -> new RuntimeException("Cannot find image " + imageName));
    }

}
