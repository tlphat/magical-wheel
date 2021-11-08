package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;

public class PlayerPanel extends JPanel {
    // /
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
        final var layout = new GridLayout(1, 3);
        setLayout(layout);
        setOpaque(false);
        // final var gbc = new GridBagConstraints();
        // gbc.anchor = GridBagConstraints.LINE_START;
        // gbc.insets = new Insets(5, 5, 5, 5);
        // gbc.gridx = 0;
        scoreLabel.setHorizontalAlignment(JLabel.RIGHT);
        add(orderLabel);
        //gbc.gridx = 1;
        add(usernameLabel);
        //gbc.gridx = 2;
        add(scoreLabel);
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

    public void setMainPlayer() {
        usernameLabel.setText(player.getUsername() + " (You)");
    }
}
