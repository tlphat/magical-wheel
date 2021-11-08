package fit.apcs.magicalwheel.server.connection;

import static fit.apcs.magicalwheel.lib.constant.EventType.JOIN_ROOM;
import static fit.apcs.magicalwheel.lib.constant.EventType.fromString;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.FULL_CONNECTION;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.OK;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.SERVER_ERROR;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.USERNAME_EXISTED;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.USERNAME_INVALID;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.WRONG_FORMAT;
import static fit.apcs.magicalwheel.lib.util.SocketWriteUtil.getMessageFromLines;
import static fit.apcs.magicalwheel.lib.util.SocketWriteUtil.writeStringToChannel;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import fit.apcs.magicalwheel.lib.util.SocketReadUtil;
import fit.apcs.magicalwheel.server.entity.Player;
import fit.apcs.magicalwheel.server.exception.DuplicatedResourceException;
import fit.apcs.magicalwheel.server.exception.WrongMessageFormatException;
import fit.apcs.magicalwheel.server.gameplay.GamePlay;

public final class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final Server INSTANCE = new Server();
    private static final int SERVER_PORT = 8080;

    private final GamePlay gamePlay = new GamePlay();

    private Server() {

    }

    public static Server getInstance() {
        return INSTANCE;
    }

    private static void closeClientChannel(AsynchronousSocketChannel channel) {
        try {
            channel.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in closing connection", ex);
        }
    }

    public void run() {
        try (final var serverChannel =
                     AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(SERVER_PORT))) {
            LOGGER.log(Level.INFO, "Server waiting for connections");
            waitingForConnections(serverChannel);
            while (!gamePlay.canStart()) {
                Thread.onSpinWait();
            }
            gamePlay.start();
            LOGGER.log(Level.INFO, "Game started");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in setting up server socket", ex);
            System.exit(1);
        }
    }

    private void waitingForConnections(AsynchronousServerSocketChannel serverChannel) {
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                LOGGER.log(Level.INFO, "New connection accepted");
                readUsername(clientChannel);
                if (!gamePlay.canStart()) {
                    serverChannel.accept(null, this);
                }
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Error in accepting connection", ex);
                if (!gamePlay.canStart()) {
                    serverChannel.accept(null, this);
                }
            }
        });
    }

    private void readUsername(AsynchronousSocketChannel clientChannel) {
        final var byteBuffer = ByteBuffer.allocate(200);
        // TODO: separate completion handler into a new class
        final var completionHandler = new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer numBytes, Void attachment) {
                try {
                    final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
                    validateEventType(reader);
                    final var username = reader.readLine().trim();
                    final var player = new Player(username, clientChannel);
                    final var players = gamePlay.addPlayer(player);
                    final var responseBody =
                            Stream.concat(Stream.of(OK.getCode(), GamePlay.MAX_NUM_PLAYERS, players.size()),
                                          players.stream().map(Player::getUsername)).toArray(Object[]::new);
                    writeStringToChannel(clientChannel, getMessageFromLines(JOIN_ROOM, responseBody));
                } catch (UnsupportedOperationException ex) { // TODO: simplify and generalize this logic
                    writeStringToChannel(clientChannel,
                                         getMessageFromLines(JOIN_ROOM, FULL_CONNECTION.getCode()), false);
                } catch (DuplicatedResourceException ex) {
                    writeStringToChannel(clientChannel,
                                         getMessageFromLines(JOIN_ROOM, USERNAME_EXISTED.getCode()), false);
                } catch (WrongMessageFormatException ex) {
                    writeStringToChannel(clientChannel,
                                         getMessageFromLines(JOIN_ROOM, WRONG_FORMAT.getCode()), false);
                } catch (IllegalArgumentException ex) {
                    writeStringToChannel(clientChannel,
                                         getMessageFromLines(JOIN_ROOM, USERNAME_INVALID.getCode()), false);
                } catch (IOException | RuntimeException ex) {
                    writeStringToChannel(clientChannel,
                                         getMessageFromLines(JOIN_ROOM, SERVER_ERROR.getCode()), false);
                }
            }

            private void validateEventType(BufferedReader reader) throws IOException {
                final var type = fromString(reader.readLine());
                if (type != JOIN_ROOM) {
                    LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                               new Object[]{ JOIN_ROOM, type});
                    throw new WrongMessageFormatException("Wrong event type");
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                LOGGER.log(Level.WARNING, "Timeout waiting for name", exc);
                closeClientChannel(clientChannel);
            }
        };
        clientChannel.read(byteBuffer, SocketReadUtil.TIMEOUT_IN_SECONDS, SECONDS, null, completionHandler);
    }

}
