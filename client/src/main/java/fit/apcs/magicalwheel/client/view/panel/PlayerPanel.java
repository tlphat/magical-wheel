package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import fit.apcs.magicalwheel.client.model.Player;

public class PlayerPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -3210186870376241901L;

    private final transient Player player;
    private final JLabel orderLabel;
    private final JLabel usernameLabel;
    private final JLabel scoreLabel;

    public PlayerPanel(Player player) {
        this.player = player;
        orderLabel = new JLabel(String.valueOf(player.getOrder()) + ".");
        usernameLabel = new JLabel(player.getUsername());
        scoreLabel = new JLabel(String.valueOf(player.getPoint()));
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
        scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(orderLabel);
        add(usernameLabel);
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
