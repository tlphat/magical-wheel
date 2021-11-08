package fit.apcs.magicalwheel.client.view.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class GameInfoPanel extends JPanel {

    private final JLabel keywordLabel;
    private final JLabel hintLabel;
    private final JLabel turnLabel;
    private final JLabel countdownLabel;

    public GameInfoPanel(int keywordLength, String hint) {
        keywordLabel = new JLabel();
        hintLabel = new JLabel();
        turnLabel = new JLabel();
        countdownLabel = new JLabel();
        hintLabel.setText("Hint: " + hint);
        setNewKeyword(new String(new char[10]).replace('\0', '*'));
        setTurn(1);
        setTime("00:00");
        setOpaque(false);
        setLayout(new BorderLayout());
        add(keywordPanel(), BorderLayout.WEST);
        add(timePanel(), BorderLayout.EAST);
    }

    private void setTime(String time) {
        countdownLabel.setText(time);
    }

    private void setTurn(int i) {
        turnLabel.setText("Turn " + String.valueOf(i));
    }

    public void setNewKeyword(String keyword) {
        keywordLabel.setText("Keyword: " + keyword);
    }

    private JPanel timePanel() {
        final var timePanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10,0,0,0);
        turnLabel.setForeground(Color.WHITE);
        countdownLabel.setForeground(Color.WHITE);
        timePanel.setOpaque(false);
        timePanel.add(turnLabel, gbc);
        timePanel.add(countdownLabel, gbc);
        return timePanel;
    }

    private JPanel keywordPanel() {
        final var keywordPanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(10,0,0,0);
        keywordLabel.setForeground(Color.WHITE);
        hintLabel.setForeground(Color.WHITE);
        keywordPanel.setOpaque(false);
        keywordPanel.add(keywordLabel, gbc);
        keywordPanel.add(hintLabel, gbc);
        return keywordPanel;
    }
}
