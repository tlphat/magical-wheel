package fit.apcs.magicalwheel.client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.client.connection.handler.JoinGameHandler;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.panel.WaitingPanel;
import fit.apcs.magicalwheel.client.view.panel.WelcomePanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;
import fit.apcs.magicalwheel.lib.util.SocketWriteUtil;

public final class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final Client INSTANCE = new Client();

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static final int TIMEOUT = 10;  // in seconds

    private AsynchronousSocketChannel channel;

    private Client() {
        openChannel();
    }

    private void openChannel() {
        try {
            channel = AsynchronousSocketChannel.open();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot open socket channel", ex);
            System.exit(1);
        }
    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public void openConnection(Consumer<Void> onSuccessfulConnection) {
        if (!channel.isOpen()) {
            openChannel();
        }
        channel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), null,
                        new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                onSuccessfulConnection.accept(null);
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.SEVERE, "Cannot connect to server", ex);
            }
        });
    }

    public void closeConnection() {
        try {
            channel.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in closing connection", ex);
        }
    }

    public void sendUsername(String username, WelcomePanel panel) {
        final var message = SocketReadUtil.getMessageFromLines(EventType.JOIN_ROOM, username.trim());
        SocketWriteUtil.writeStringToChannel(channel, message);
        waitForJoinGameResponse(panel);
    }

    private void waitForJoinGameResponse(WelcomePanel panel) {
        final var byteBuffer = ByteBuffer.allocate(1000);
        final var responseHandler = new JoinGameHandler(byteBuffer, panel, this);
        channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, responseHandler);
    }

    public void waitForStartGameSignal(WaitingPanel panel) {
        final var byteBuffer = ByteBuffer.allocate(2000);
        // TODO: separate the handler into a new class
        final var responseHandler = new CompletionHandler<Integer, Void>() {
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
                            channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, this);
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
                }
            }

            private void handleStartGameSignal(BufferedReader reader, WaitingPanel panel) throws IOException {
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
                channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, this);
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                // If timeout, we continue to listen to start game signal
                channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, this);
            }
        };
        channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, responseHandler);
    }

}
