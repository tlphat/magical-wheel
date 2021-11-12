package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.panel.FinishPanel;
import fit.apcs.magicalwheel.client.view.panel.GamePanel;
import fit.apcs.magicalwheel.client.view.panel.WaitingPanel;
import fit.apcs.magicalwheel.client.view.panel.WelcomePanel;
import fit.apcs.magicalwheel.client.view.util.ResourceUtil;

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
        List<Player> players = new ArrayList<>();
        players.add(new Player(1, "dungplt"));
        players.add(new Player(2, "tlp"));
        players.add(new Player(3, "huhu"));
        players.add(new Player(4, "thao"));
        players.add(new Player(5, "ok"));
        players.add(new Player(6, "ok"));
        //switchToGamePanel(10, "This is a very very very very very very very very very very very very long hint.", players, 4);
        switchToFinishPanel("dungplt", "keywordne", players);
        setBackground(Color.BLACK);
        setVisible(true);
    }

    private void switchToFinishPanel(String winner, String keyword, List<Player> players) {
        final var finishPanel = new FinishPanel(winner, keyword, players);
        setContentPane(finishPanel);
        refresh();
    }

    public void switchToWaitingRoom(int maxNumPlayers, List<Player> currentPlayers) {
        final var waitingPanel = new WaitingPanel(maxNumPlayers, currentPlayers, this);
        setContentPane(waitingPanel);
        refresh();
    }

    public void refresh() {
        repaint();
        revalidate();
    }

    public void switchToGamePanel(int keywordLength, String hint, List<Player> players, int curPlayerOrder) {
        final var gamePanel = new GamePanel(keywordLength, hint, players, curPlayerOrder);
        setContentPane(gamePanel);
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
        final var welcomePanel = new WelcomePanel(this);
        setContentPane(welcomePanel);
    }

}
