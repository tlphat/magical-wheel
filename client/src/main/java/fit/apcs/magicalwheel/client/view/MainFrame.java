package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serial;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fit.apcs.magicalwheel.client.connection.Client;

public class MainFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 7641338013687300073L;
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class.getName());


    public MainFrame() {
        setTitle(ResourceUtil.GAME_NAME);
        setOnExitEvent();
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setIconImage(new ImageIcon(ResourceUtil.getImageURL("wheel.png")).getImage());
        getContentPane().setBackground(Color.BLACK);
        addWelcomePanel();
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

    private void addWelcomePanel() {
        final var welcomePanel = new WelcomePanel();
        add(welcomePanel);
    }
}
