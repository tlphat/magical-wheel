package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;

public class FinishPanel extends JPanel {

    private final String winner;
    private final String keyword;
    private final List<PlayerPanel> playerPanels;

    public FinishPanel(String winner, String keyword, List<Player> players) {
        this.winner = winner;
        this.keyword = keyword;
        this.playerPanels = new ArrayList<>();
        for (Player player: players) {
            playerPanels.add(new PlayerPanel(player));
        }
        initLayout();
    }

    private void initLayout() {
        final var layout = new FlowLayout();
        layout.setHgap(30);
        layout.setVgap(30);
        setOpaque(false);
        setLayout(layout);
        setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        add(contentPanel());
    }

    private JPanel contentPanel() {
        final var panel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        panel.setOpaque(false);
        panel.add(title(), gbc);
        panel.add(gameResult(), gbc);
        panel.add(playerList(), gbc);
        return panel;
    }

    private JPanel playerList() {
        final var panel = new JPanel();
        final var layout = new GridLayout(playerPanels.size(), 1);
        layout.setVgap(10);
        panel.setOpaque(false);
        panel.setLayout(layout);
        for (PlayerPanel playerPanel: playerPanels) {
            panel.add(playerPanel);
        }
        return panel;
    }

    private JPanel gameResult() {
        final var panel = new JPanel();
        panel.setOpaque(false);
        panel.add(textPanel("Winner: ", winner));
        panel.add(textPanel("Keyword: ", keyword));
        return panel;
    }

    private JPanel textPanel(String labelName, String value) {
        final var panel = new JPanel();
        final var label = new JLabel(labelName);
        final var valueLabel = new JLabel(value);
        label.setForeground(Color.WHITE);
        valueLabel.setForeground(Color.CYAN);
        panel.setOpaque(false);
        panel.add(label);
        panel.add(valueLabel);
        return panel;
    }

    private JLabel title() {
        final var label = new JLabel("END GAME");
        label.setForeground(Color.WHITE);
        return label;
    }
}
