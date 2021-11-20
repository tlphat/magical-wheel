package fit.apcs.magicalwheel.client.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;
import java.util.List;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.timer.RoundTimerTask;
import fit.apcs.magicalwheel.client.view.MainFrame;

public class GamePanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -13480572595592699L;

    private final MainFrame mainFrame;
    private final JLabel messageLabel;
    private final ScoreboardPanel scoreboardPanel;
    private final GameInfoPanel gameInfoPanel;
    private final SubmitPanel submitPanel;
    private final JLabel countdownLabel;
    private final JLabel turnLabel;
    private final double countdown;
    private final int maxTurn;
    private final transient List<Player> players;
    private final transient Player mainPlayer;
    private boolean isKeywordGuessed = false;
    private transient Timer timer = new Timer();

    public GamePanel(int keywordLength, String hint, double countdown, int maxTurn,
                     List<Player> players, int mainPlayerOrder, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.countdown = countdown;
        this.maxTurn = maxTurn;
        this.players = players;
        messageLabel = messageLabel();
        mainPlayer = players.get(mainPlayerOrder - 1);

        scoreboardPanel = new ScoreboardPanel(players, mainPlayerOrder, mainFrame);
        gameInfoPanel = new GameInfoPanel(keywordLength, hint);
        submitPanel = new SubmitPanel(this, mainFrame);
        countdownLabel = new JLabel();
        turnLabel = new JLabel();

        initLayout();
        resetMessage();
        setTime((int) countdown);
        Client.getInstance().listenToStartTurnSignal(this);
    }

    @SuppressWarnings("MethodMayBeStatic")
    private JLabel messageLabel() {
        final var label = new JLabel();
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Calibri", Font.ITALIC, 18));
        return label;
    }

    private void resetMessage() {
        messageLabel.setText("There's not any character guess for this keyword.");
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
        mainGamePanel.add(messagePanel(), gbc);
        mainGamePanel.add(gameInfoPanel, gbc);
        mainGamePanel.add(submitPanel, gbc);
        return mainGamePanel;
    }

    private JPanel messagePanel() {
        final var panel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        final var line = new JLabel("_________________________________________________________");
        line.setFont(new Font("Calibri", Font.ITALIC, 15));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        line.setForeground(Color.WHITE);
        panel.setOpaque(false);
        panel.add(messageLabel, gbc);
        panel.add(line, gbc);
        return panel;
    }

    public synchronized void setTime(int timeInSec) {
        countdownLabel.setText(convertTimeToString(timeInSec));
        mainFrame.refresh();
    }

    private synchronized String convertTimeToString(int timeInSec) {
        var mins = String.valueOf(timeInSec / 60);
        var secs = String.valueOf(timeInSec % 60);
        if (timeInSec / 60 < 10) {
            mins = '0' + mins;
        }
        if (timeInSec % 60 < 10) {
            secs = '0' + secs;
        }
        return mins + ':' + secs;
    }

    private synchronized void setTurn(int turn) {
        turnLabel.setText(String.format("Turn %d/%d", turn, maxTurn));
        mainFrame.refresh();
    }

    public synchronized void startTurn(String username, int turn) {
        if (username.equals(mainPlayer.getUsername())) {
            submitPanel.disableSubmission(true);
            if (turn <= 2) {
                submitPanel.disableKeywordField();
            }
        } else {
            submitPanel.disableSubmission(false);
        }
        prepareForTurn(username, turn);
    }

    private void prepareForTurn(String username, int turn) {
        setTurn(turn);
        setTime((int) countdown);
        startTimer();
        Client.getInstance().waitForGuessResponse(this);
        scoreboardPanel.setCurrentPlayer(username);
        mainFrame.refresh();
    }

    private void startTimer() {
        timer = new Timer();
        final var timerTask = new RoundTimerTask((int) countdown, this, timer);
        final var delay = 1000; // in ms
        final var timeToNextTask = 1000; // in ms
        timer.scheduleAtFixedRate(timerTask, delay, timeToNextTask);
    }

    public void updateScore(String username, int score) {
        scoreboardPanel.updateScore(username, score);
    }

    public void updateKeyword(String keyword) {
        gameInfoPanel.setNewKeyword(keyword);
    }

    public synchronized void updateMessage(String username, String character, String keyword) {
        var message = '<' + username + "> ";
        if (character.isEmpty()) {
            message += "time out";
        }
        else {
            message += "guess character '" + character + '\'';
            if (!keyword.isEmpty()) {
                message += "and keyword '" + keyword + '\'';
            }
        }
        messageLabel.setText(message);
    }

    public synchronized void keywordGotGuessed() {
        isKeywordGuessed = true;
    }

    public synchronized void eliminatePlayer(String username) {
        scoreboardPanel.eliminatePlayer(username);
    }

    public synchronized void disableSubmitButton() {
        submitPanel.disableSubmission(false);
    }

    public synchronized void cancelTimer() {
        timer.cancel();
    }

    public synchronized void switchToFinishGame(boolean isCompletedKeyword, String winner,
                                                String keyword, int numPlayers, List<Integer> listScore) {
        verifyIfKeywordHasBeenGuessed(isCompletedKeyword);
        verifyWinnerUsernameIsValid(winner);
        verifyNumPlayers(numPlayers);
        verifyPlayersScore(listScore);
        mainFrame.switchToFinishGame(winner, keyword, players, mainPlayer);
    }

    private void verifyIfKeywordHasBeenGuessed(boolean isCompletedKeyword) {
        if (isCompletedKeyword != isKeywordGuessed) {
            throw new IllegalStateException("Conflict in whether the keyword has been guessed");
        }
    }

    private void verifyWinnerUsernameIsValid(String winner) {
        if (!winner.isEmpty()
            && players.stream().noneMatch(player -> player.getUsername().equals(winner))) {
            throw new IllegalStateException("Winner username not found");
        }
    }

    private void verifyNumPlayers(int numPlayers) {
        if (players.size() != numPlayers) {
            throw new IllegalStateException("Number of players is not correct");
        }
    }

    private void verifyPlayersScore(List<Integer> listScore) {
        for (var index = 0; index < players.size(); ++index) {
            if (players.get(index).getPoint() != listScore.get(index)) {
                throw new IllegalStateException("There is something wrong with end game score");
            }
        }
    }

}
