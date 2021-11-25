package fit.apcs.magicalwheel.client.connection.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.panel.WaitingPanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.constant.StatusCode;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;

public class StartGameHandler implements CompletionHandler<Integer, Void> {

    private static final Logger LOGGER = Logger.getLogger(StartGameHandler.class.getName());

    private final ByteBuffer byteBuffer;
    private final WaitingPanel panel;
    private final AsynchronousSocketChannel channel;

    public StartGameHandler(ByteBuffer byteBuffer, WaitingPanel panel, AsynchronousSocketChannel channel) {
        this.byteBuffer = byteBuffer;
        this.panel = panel;
        this.channel = channel;
    }

    @Override
    public void completed(Integer numBytes, Void attachment) {
        LOGGER.log(Level.INFO, "Response:\n{0}", SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            final var type = validateEventType(reader);
            switch (type) {
                case START_GAME -> handleStartGameSignal(reader, panel);
                case JOIN_ROOM -> handleJoinGameSignal(reader, panel);
                case LEAVE_GAME -> handleLeaveGameSignal(reader, panel);
                default -> clearAndReadBuffer();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
        }
    }

    private static EventType validateEventType(BufferedReader reader) throws IOException {
        final var type = EventType.fromString(reader.readLine());
        if (type == null) {
            throw new IOException("Cannot parse the event type");
        }
        return type;
    }

    private static void handleStartGameSignal(BufferedReader reader, WaitingPanel panel) throws IOException {
        final var keywordLength = Integer.parseInt(reader.readLine());
        final var hint = reader.readLine();
        final var countdown = Double.parseDouble(reader.readLine());
        final var maxTurn = Integer.parseInt(reader.readLine());
        final var players = readListPlayers(reader);
        SwingUtilities.invokeLater(() -> panel.startGame(keywordLength, hint, countdown, maxTurn, players));
    }

    private void handleJoinGameSignal(BufferedReader reader, WaitingPanel panel) throws IOException {
        if (!statusIsOK(reader)) {
            return;
        }
        verifyMaxNumPlayersLineExist(reader);
        final var listPlayers = readListPlayers(reader);
        SwingUtilities.invokeLater(() -> panel.updateListPlayers(listPlayers));
        clearAndReadBuffer();
    }

    private static boolean statusIsOK(BufferedReader reader) throws IOException {
        final var statusCode = StatusCode.fromString(reader.readLine());
        return statusCode == StatusCode.OK;
    }

    private static void verifyMaxNumPlayersLineExist(BufferedReader reader) throws IOException {
        final var line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("The message format is not correct");
        }
    }

    private static List<Player> readListPlayers(BufferedReader reader) throws IOException {
        final var numPlayers = Integer.parseInt(reader.readLine());
        final var players = new ArrayList<Player>();
        for (var order = 1; order <= numPlayers; ++order) {
            final var username = reader.readLine().trim();
            players.add(new Player(order, username));
        }
        return players;
    }

    private void handleLeaveGameSignal(BufferedReader reader, WaitingPanel panel) throws IOException {
        final var usernameWhoLeaveRoom = reader.readLine().trim();
        SwingUtilities.invokeLater(() -> panel.removePlayer(usernameWhoLeaveRoom));
        clearAndReadBuffer();
    }

    private void clearAndReadBuffer() {
        byteBuffer.clear();
        channel.read(byteBuffer, null, this);
    }

    @Override
    public void failed(Throwable ex, Void attachment) {
        LOGGER.log(Level.WARNING, "Cannot get response from server", ex);
    }

}
