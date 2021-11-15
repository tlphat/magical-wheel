package fit.apcs.magicalwheel.client.connection;

import static fit.apcs.magicalwheel.lib.constant.EventType.PLAYER_GUESS;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.client.connection.handler.JoinGameHandler;
import fit.apcs.magicalwheel.client.connection.handler.StartGameHandler;
import fit.apcs.magicalwheel.client.connection.handler.StartTurnHandler;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.panel.GamePanel;
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
        final var message = SocketWriteUtil.getMessageFromLines(EventType.JOIN_ROOM, username.trim());
        SocketWriteUtil.writeStringToChannel(channel, message);
        waitForJoinGameResponse(panel);
    }

    private void waitForJoinGameResponse(WelcomePanel panel) {
        final var byteBuffer = ByteBuffer.allocate(1000);
        final var responseHandler = new JoinGameHandler(byteBuffer, panel);
        channel.read(byteBuffer, SocketReadUtil.TIMEOUT_IN_SECONDS, TimeUnit.SECONDS, null, responseHandler);
    }

    public void waitForStartGameSignal(WaitingPanel panel) {
        final var byteBuffer = ByteBuffer.allocate(2000);
        final var responseHandler = new StartGameHandler(byteBuffer, panel, channel);
        channel.read(byteBuffer, null, responseHandler);
    }

    public void listenToStartTurnSignal(GamePanel panel) {
        final var byteBuffer = ByteBuffer.allocate(1000);
        final var responseHandler = new StartTurnHandler(byteBuffer, panel, channel);
        channel.read(byteBuffer, null, responseHandler);
    }

    public void submitGuess(GamePanel panel, String guessChar, String keyword, Player mainPlayer) {
        final var message = SocketWriteUtil.getMessageFromLines(PLAYER_GUESS, guessChar, keyword);
        SocketWriteUtil.writeStringToChannel(channel, message);
        waitForGuessResponse(panel, mainPlayer);
    }

    public void waitForGuessResponse(GamePanel panel, Player mainPlayer) {
        final var byteBuffer = ByteBuffer.allocate(1000);
        final var responseHandler = new CompletionHandler<Integer, Void>() {
            @Override
            @SuppressWarnings("unused")
            public void completed(Integer numBytes, Void attachment) {
                LOGGER.log(Level.INFO, "Response:\n{0}", SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
                try {
                    final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
                    validateEventType(reader);
                    final var username = reader.readLine().trim();
                    final var guessChar = reader.readLine();
                    final var guessKeyword = reader.readLine();
                    final var keyword = reader.readLine();
                    final var score = Integer.parseInt(reader.readLine());
                    final var isCorrectKeyWord = Integer.parseInt(reader.readLine()) != 0;
                    final var isEnd = Integer.parseInt(reader.readLine()) != 0;
                    if (mainPlayer.getUsername().equals(username)) {
                        listenToStartTurnSignal(panel);
                    } else {
                        panel.updateScore(username, score);
                        panel.updateKeyword(keyword);
                        if (!guessKeyword.isEmpty()) {
                            if (isCorrectKeyWord) {
                                panel.keywordGotGuessed();
                            } else {
                                panel.eliminatePlayer(username);
                            }
                        }
                    }
                    // TODO: handle end game signal
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
                }
            }

            private void validateEventType(BufferedReader reader) throws IOException {
                final var type = EventType.fromString(reader.readLine());
                if (type != PLAYER_GUESS) {
                    LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                               new Object[]{ PLAYER_GUESS, type});
                    clearAndReadBuffer();
                    throw new IOException("Event type is not correct");
                }
            }

            private void clearAndReadBuffer() {
                byteBuffer.clear();
                channel.read(byteBuffer, null, this);
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Cannot get response from server", ex);
            }
        };
        channel.read(byteBuffer, null, responseHandler);
    }

}
