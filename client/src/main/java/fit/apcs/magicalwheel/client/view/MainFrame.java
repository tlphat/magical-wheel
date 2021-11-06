package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fit.apcs.magicalwheel.client.connection.Client;

public class MainFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 7641338013687300073L;

    public MainFrame() {
        setTitle(ResourceUtil.GAME_NAME);
        setOnExitEvent();
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setIconImage(new ImageIcon(ResourceUtil.getImageURL("wheel.png")).getImage());
        //addWelcomePanel();
        switchToWaitingRoom();
        setBackground(Color.BLACK);
        setVisible(true);
    }

    private void switchToWaitingRoom() {
        final var waitingPanel = new WaitingPanel(new Player("use", 1));
        setContentPane(waitingPanel);
    }

    private void setOnExitEvent() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Client.getInstance().closeConnection();
                System.exit(0);
            }
        });
    }

    private void addWelcomePanel() {
        final var welcomePanel = new WelcomePanel();
        setContentPane(welcomePanel);
    }
}
