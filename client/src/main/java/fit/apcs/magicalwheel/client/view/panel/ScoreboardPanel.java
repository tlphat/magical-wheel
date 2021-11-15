package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.MainFrame;

public class ScoreboardPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -3608692024568041019L;

    private List<PlayerPanel> playerPanels;
    private final int mainPlayerOrder;
    private final MainFrame mainFrame;

    public ScoreboardPanel(List<Player> players, int mainPlayerOrder, MainFrame mainFrame) {
        this.mainPlayerOrder = mainPlayerOrder;
        this.mainFrame = mainFrame;
        final var layout = new FlowLayout();
        initPlayerPanels(players);
        layout.setHgap(30);
        layout.setVgap(30);
        setOpaque(false);
        setLayout(layout);
        setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        setMainPlayer();
        add(contentPanel());
    }

    private void initPlayerPanels(List<Player> players) {
        playerPanels = new ArrayList<>();
        for (Player player: players) {
            playerPanels.add(new PlayerPanel(player, mainFrame));
        }
    }

    private JPanel contentPanel() {
        final var contentPanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10,0,0,0);
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel(), gbc);
        contentPanel.add(playerList(), gbc);
        return contentPanel;
    }

    private JPanel playerList() {
        final var playerList = new JPanel();
        final var layout = new GridLayout(playerPanels.size(), 1);
        layout.setVgap(10);
        playerList.setOpaque(false);
        playerList.setLayout(layout);
        for (PlayerPanel playerPanel: playerPanels) {
            playerList.add(playerPanel);
        }
        return playerList;
    }

    @SuppressWarnings("MethodMayBeStatic")
    private JPanel headerPanel() {
        final var headerPanel = new JPanel();
        final var title = new JLabel("SCOREBOARD");
        title.setForeground(Color.YELLOW);
        headerPanel.setOpaque(false);
        headerPanel.add(title);
        return headerPanel;
    }

    private void setMainPlayer() {
        playerPanels.get(mainPlayerOrder - 1).setMainPlayer();
    }

    public void setCurrentPlayer(String username) {
        for (PlayerPanel panel: playerPanels) {
            if (panel.getPlayer().getUsername().equals(username)) {
                panel.switchToPlayerTurn();
            }
            else {
                panel.unSwitchToPlayerTurn();
            }
        }
    }

    public void updateScore(String username, int score) {
        for (PlayerPanel panel: playerPanels) {
            if (panel.getPlayer().getUsername().equals(username)) {
                panel.updateScore(score);
            }
        }
    }

    public void eliminatePlayer(String username) {
        for (PlayerPanel panel: playerPanels) {
            if (panel.getPlayer().getUsername().equals(username)) {
                panel.eliminate();
            }
        }
    }

}
