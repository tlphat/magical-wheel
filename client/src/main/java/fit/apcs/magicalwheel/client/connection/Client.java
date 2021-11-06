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

import fit.apcs.magicalwheel.client.constant.EventType;
import fit.apcs.magicalwheel.client.constant.StatusCode;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.WelcomePanel;

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
        final var message = SocketUtil.getMessageFromLines(EventType.JOIN_ROOM, username.trim());
        SocketUtil.writeStringToChannel(channel, message);
        waitForJoinGameResponse(panel);
    }

    private void waitForJoinGameResponse(WelcomePanel panel) {
        // TODO: separate the completion handler into a new class
        final var byteBuffer = ByteBuffer.allocate(100);
        final var responseHandler = new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer numBytes, Void attachment) {
                LOGGER.log(Level.INFO, "Response:\n{0}", SocketUtil.byteBufferToString(byteBuffer, numBytes));
                try {
                    final var reader = SocketUtil.byteBufferToReader(byteBuffer, numBytes);
                    verifyEventType(reader);
                    verifyReturnCode(reader);
                    joinWaitingRoom(reader);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
                    panel.setMessage(StatusCode.WRONG_FORMAT.getMessage());
                    closeConnection();
                } catch (RuntimeException ex) {
                    LOGGER.log(Level.SEVERE, "Response is not OK", ex);
                    closeConnection();
                }
            }

            private void verifyEventType(BufferedReader reader) throws IOException {
                final var type = EventType.fromString(reader.readLine());
                if (type != EventType.JOIN_ROOM) {
                    LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                               new Object[]{EventType.JOIN_ROOM, type});
                    throw new IllegalArgumentException("Wrong event type");
                }
            }

            private void verifyReturnCode(BufferedReader reader) throws IOException {
                final var statusCode = StatusCode.fromString(reader.readLine());
                if (statusCode != StatusCode.OK) {
                    panel.setMessage(statusCode.getMessage());
                    throw new IllegalStateException(statusCode.getMessage());
                }
            }

            private void joinWaitingRoom(BufferedReader reader) throws IOException {
                final var maxNumPlayers = Integer.parseInt(reader.readLine());
                final var curNumPlayers = Integer.parseInt(reader.readLine());
                final var listPlayers = new ArrayList<Player>();
                for (var order = 0; order < curNumPlayers; ++order) {
                    final var username = reader.readLine().trim();
                    listPlayers.add(new Player(order, username));
                }
                panel.joinWaitingRoom(maxNumPlayers, listPlayers);
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Cannot get response from server", ex);
            }
        };
        channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, responseHandler);
    }

}
