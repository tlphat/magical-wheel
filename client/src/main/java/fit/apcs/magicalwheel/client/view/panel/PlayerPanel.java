package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;

public class PlayerPanel extends JPanel {
    
    private final Player player;
    private final JLabel orderLabel;
    private final JLabel usernameLabel;
    private final JLabel scoreLabel;

    public PlayerPanel(Player player) {
        this.player = player;
        this.orderLabel = new JLabel(String.valueOf(player.getOrder()));
        this.usernameLabel = new JLabel(player.getUsername());
        this.scoreLabel = new JLabel(String.valueOf(player.getPoint()));
        setLabelsColor();
        initLayout();
    }

    private void setLabelsColor() {
        orderLabel.setForeground(Color.WHITE);
        usernameLabel.setForeground(Color.WHITE);
        scoreLabel.setForeground(Color.WHITE);
    }

    private void initLayout() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        final var gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;
        add(orderLabel, gbc);
        gbc.weightx = 10;
        add(usernameLabel, gbc);
        gbc.weightx = 1;
        add(scoreLabel, gbc);
    }

    public void switchToPlayerTurn() {
        usernameLabel.setForeground(Color.CYAN);
    }

    public void unSwitchToPlayerTurn() {
        usernameLabel.setForeground(Color.WHITE);
    }

    public void updateScore() {
        scoreLabel.setText(String.valueOf(player.getPoint()));
    }
}
