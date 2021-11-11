package fit.apcs.magicalwheel.client.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;

public class GamePanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -13480572595592699L;

    private final ScoreboardPanel scoreboardPanel;
    private final GameInfoPanel gameInfoPanel;
    private final SubmitPanel submitPanel;
    private final JLabel countdownLabel;
    private final JLabel turnLabel;

    public GamePanel(int keywordLength, String hint, List<Player> players, int mainPlayerOrder) {
        scoreboardPanel = new ScoreboardPanel(players, mainPlayerOrder);
        gameInfoPanel = new GameInfoPanel(keywordLength, hint);
        submitPanel = new SubmitPanel();
        countdownLabel = new JLabel();
        turnLabel = new JLabel();

        initLayout();
        setTime("00:00");
        setTurn(1);
    }

    private void initLayout() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        add(contentPanel());
    }

    private JPanel contentPanel() {
        final var contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(900, 400));
        contentPanel.setOpaque(false);
        contentPanel.add(scoreboardPanel, BorderLayout.WEST);
        contentPanel.add(mainGamePanel());
        contentPanel.add(timePanel(), BorderLayout.EAST);
        return contentPanel;
    }

    private JPanel timePanel() {
        final var panel = new JPanel(new GridBagLayout());
        final var timePanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10,0,0,0);
        turnLabel.setForeground(Color.WHITE);
        countdownLabel.setForeground(Color.WHITE);
        timePanel.setPreferredSize(new Dimension(80, 80));
        timePanel.setOpaque(false);
        timePanel.add(turnLabel, gbc);
        timePanel.add(countdownLabel, gbc);
        timePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panel.setOpaque(false);
        panel.add(timePanel);
        return panel;
    }

    private JPanel mainGamePanel() {
        final var mainGamePanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(50, 10, 10, 10);
        mainGamePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainGamePanel.setOpaque(false);
        mainGamePanel.add(gameInfoPanel, gbc);
        mainGamePanel.add(submitPanel, gbc);
        return mainGamePanel;
    }

    private void setTime(String time) {
        countdownLabel.setText(time);
    }

    private void setTurn(int turn) {
        turnLabel.setText("Turn " + turn);
    }

}