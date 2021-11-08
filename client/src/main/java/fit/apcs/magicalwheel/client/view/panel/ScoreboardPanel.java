package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;

public class ScoreboardPanel extends JPanel {

    List<PlayerPanel> playerPanels;

    public ScoreboardPanel(List<Player> players) {
        final var mainPanel = new JPanel();
        final var layout = new FlowLayout();
        initPlayerPanels(players);
        layout.setHgap(30);
        layout.setVgap(30);
        mainPanel.setOpaque(false);
        mainPanel.setLayout(layout);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        mainPanel.add(contentPanel());
    }

    private void initPlayerPanels(List<Player> players) {
        playerPanels = new ArrayList<>();
        for (Player player: players) {
            playerPanels.add(new PlayerPanel(player));
        }
    }

    private JPanel contentPanel() {
        final var contentPanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20,0,0,0);
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel(), gbc);
        contentPanel.add(playerList(), gbc);
        return contentPanel;
    }

    private JPanel playerList() {
        final var playerList = new JPanel();
        playerList.setOpaque(false);
        for (PlayerPanel playerPanel: playerPanels) {
            playerList.add(playerPanel);
        }
        return playerList;
    }

    private JPanel headerPanel() {
        final var headerPanel = new JPanel();
        final var title = new JLabel("SCOREBOARD");
        headerPanel.add(title);
        return headerPanel;
    }
}
