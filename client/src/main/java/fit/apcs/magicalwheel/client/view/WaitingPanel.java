package fit.apcs.magicalwheel.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.FlowView;

public class WaitingPanel extends JPanel {
    private Player mainPlayer;
    private List<Player> waitingPlayers;
    private JLabel message;
    private int remainPlayers;

    public WaitingPanel(Player mainPlayer) {
        this.mainPlayer = mainPlayer;
        this.remainPlayers = 3;

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
        final var contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(roomHeader(), BorderLayout.NORTH);
        contentPanel.add(waitingList(), BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel waitingList() {
        final var waitingList = new JPanel();
        return waitingList;
    }

    private JPanel roomHeader() {
        final var roomHeader = new JPanel(new GridLayout(2, 1));
        roomHeader.setOpaque(false);
        roomHeader.add(roomTitle());
        roomHeader.add(roomMessage());
        return roomHeader;
    }

    private JLabel roomMessage() {
        message = new JLabel("Please wait for more " + remainPlayers + " players more to join ...");
        message.setForeground(Color.WHITE);
        return message;
    }

    private JLabel roomTitle() {
        final var roomTitle = new JLabel("Waiting Room");
        roomTitle.setForeground(Color.YELLOW);
        return roomTitle;
    }

}
