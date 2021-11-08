package fit.apcs.magicalwheel.client.connection.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.panel.WaitingPanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
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
        LOGGER.log(Level.INFO, "Response:\n{0}",
                   SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            final var type = EventType.fromString(reader.readLine());
            if (type == null) {
                throw new IOException("Cannot parse the event type");
            }
            switch (type) {
                case START_GAME:
                    handleStartGameSignal(reader, panel);
                    return;
                case NEW_PLAYER:
                    handleNewPlayerSignal(reader, panel); // intentionally fall through
                default: // continue waiting for signal
                    channel.read(byteBuffer, Client.TIMEOUT, TimeUnit.SECONDS, null, this);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
        }
    }

    private static void handleStartGameSignal(BufferedReader reader, WaitingPanel panel) throws IOException {
        final var keywordLength = Integer.parseInt(reader.readLine());
        final var hint = reader.readLine();
        final var numPlayers = Integer.parseInt(reader.readLine());
        final var players = new ArrayList<Player>();
        for (var order = 1; order <= numPlayers; ++order) {
            final var username = reader.readLine().trim();
            players.add(new Player(order, username));
        }
        panel.startGame(keywordLength, hint, players);
    }

    private void handleNewPlayerSignal(BufferedReader reader, WaitingPanel panel) throws IOException {
        final var newPlayerUsername = reader.readLine().trim();
        panel.addNewPlayerToRoom(newPlayerUsername);
        channel.read(byteBuffer, Client.TIMEOUT, TimeUnit.SECONDS, null, this);
    }

    @Override
    public void failed(Throwable ex, Void attachment) {
        // If timeout, we continue to listen to start game signal
        channel.read(byteBuffer, Client.TIMEOUT, TimeUnit.SECONDS, null, this);
    }

}
