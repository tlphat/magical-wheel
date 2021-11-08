package fit.apcs.magicalwheel.client.view.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.FlowView;

import fit.apcs.magicalwheel.client.model.Player;

public class GamePanel extends JPanel {

    private final int keywordLength;
    private final String hint;
    private final List<Player> players;
    private final int mainPlayerOrder;
    private final ScoreboardPanel scoreboardPanel;
    private final GameInfoPanel gameInfoPanel;

    public GamePanel(int keywordLength, String hint, List<Player> players, int mainPlayerOrder) {
        this.scoreboardPanel = new ScoreboardPanel(players, mainPlayerOrder);
        this.gameInfoPanel = new GameInfoPanel(keywordLength, hint);
        this.keywordLength = keywordLength;
        this.hint = hint;
        this.players = players;
        this.mainPlayerOrder = mainPlayerOrder;
        initLayout();
    }

    private void initLayout() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        add(contentPanel());
    }

    private JPanel contentPanel() {
        final var contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(800, 400));
        contentPanel.setOpaque(false);
        contentPanel.add(scoreboardPanel, BorderLayout.WEST);
        contentPanel.add(mainGamePanel());
        return contentPanel;
    }

    private JPanel mainGamePanel() {
        final var mainGamePanel = new JPanel(new GridLayout(2, 1));
        mainGamePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainGamePanel.setOpaque(false);
        mainGamePanel.add(gameInfoPanel);
        return mainGamePanel;
    }

}