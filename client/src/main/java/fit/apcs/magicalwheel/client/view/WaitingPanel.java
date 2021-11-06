package fit.apcs.magicalwheel.client.view;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.SwingConstants;

public class WaitingPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -9181686575965110043L;
    private static final int PLAY_ELEMENT_HEIGHT = 30;
    private static final int MAX_PLAYERS = 10;

    private final transient Player mainPlayer;
    private final transient List<Player> waitingPlayers;
    private JLabel message;
    private JPanel waitingList;
    private int currentNumber;

    public WaitingPanel(List<String> currentPlayers) {        
        //map usernames from server to Player
        waitingPlayers = new ArrayList<>();
        int order = 1;
        for (String username: currentPlayers) {
            waitingPlayers.add(new Player(username, order++));
        }
        currentNumber = waitingPlayers.size();
        mainPlayer = waitingPlayers.get(currentNumber - 1);
        
        setOpaque(false);
        setLayout(new GridBagLayout());
        add(mainPanel());
    }

    private JPanel mainPanel() {
        final var mainPanel = new JPanel();
        final var layout = new FlowLayout();
        layout.setHgap(30);
        layout.setVgap(30);
        mainPanel.setOpaque(false);
        mainPanel.setLayout(layout);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW));
        mainPanel.add(contentPanel());
        return mainPanel;
    }

    private JPanel contentPanel() {
        final var contentPanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20,0,0,0);

        contentPanel.setOpaque(false);
        contentPanel.add(roomHeader(), gbc);
        contentPanel.add(waitingList(), gbc);
        return contentPanel;
    }

    private JPanel waitingList() {
        waitingList = new JPanel(new GridLayout(MAX_PLAYERS, 1));
        waitingList.setPreferredSize(new Dimension(300, PLAY_ELEMENT_HEIGHT * MAX_PLAYERS));
        waitingList.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        waitingList.setOpaque(false);
        for (Player player: waitingPlayers) {
            addNewPlayerToWaitingList(player);
        }
        return waitingList;
    }

    private void addNewPlayerToWaitingList(Player player) {
        final var playerPanel = new JPanel(new GridBagLayout());
        final var orderLabel = new JLabel(String.valueOf(player.getOrder()), SwingConstants.LEFT);
        final var usernameLabel = new JLabel(player.getUsername());
        final var gbc = new GridBagConstraints();
        playerPanel.setOpaque(false);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1;
        playerPanel.add(orderLabel, gbc);
        gbc.weightx = 10;
        playerPanel.add(usernameLabel, gbc);
        if (mainPlayer == player) {
            orderLabel.setForeground(Color.YELLOW);
            usernameLabel.setForeground(Color.YELLOW);
        }
        else {
            orderLabel.setForeground(Color.WHITE);
            usernameLabel.setForeground(Color.WHITE);
        }
        waitingList.add(playerPanel);
    }

    public void addNewPlayerToRoom(String username) {
        currentNumber++;
        final var player = new Player(username, currentNumber);
        setWaitingMessage();
        addNewPlayerToWaitingList(player);
    }

    private JPanel roomHeader() {
        final var roomHeader = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        roomHeader.setOpaque(false);
        roomHeader.add(roomTitle(), gbc);
        roomHeader.add(roomMessage(), gbc);
        return roomHeader;
    }

    private JLabel roomMessage() {
        message = new JLabel();
        setWaitingMessage();
        message.setForeground(Color.WHITE);
        return message;
    }

    private void setWaitingMessage() {
        message.setText("Please wait for more " + (MAX_PLAYERS - currentNumber) + " players to join ...");
    }

    private JLabel roomTitle() {
        final var roomTitle = new JLabel("Waiting Room");
        roomTitle.setForeground(Color.YELLOW);
        roomTitle.setHorizontalTextPosition(SwingConstants.CENTER);
        return roomTitle;
    }

}
