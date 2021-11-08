package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Component;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.model.Player;

public class GamePanel extends JPanel {

    private final int keywordLength;
    private final String hint;
    private final List<Player> players;
    private final int mainPlayerOrder;
    private final ScoreboardPanel scoreboardPanel;

    public GamePanel(int keywordLength, String hint, List<Player> players, int mainPlayerOrder) {
        scoreboardPanel = new ScoreboardPanel(players);
        this.keywordLength = keywordLength;
        this.hint = hint;
        this.players = players;
        this.mainPlayerOrder = mainPlayerOrder;

        initLayout();
    }

    private void initLayout() {
        setOpaque(false);
    }
}
