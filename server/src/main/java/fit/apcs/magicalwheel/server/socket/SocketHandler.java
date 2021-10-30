package fit.apcs.magicalwheel.server.socket;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.server.entity.Player;
import fit.apcs.magicalwheel.server.gameplay.GamePlay;

public class SocketHandler {

    private static final Logger LOGGER = Logger.getLogger(SocketHandler.class.getName());
    private static final String OK_MESSAGE = "OK";
    private static final int SERVER_PORT = 8080;
    private static final long READ_TIMEOUT = 10; // in seconds

    private final GamePlay gamePlay = new GamePlay();

    public void runServer() {
        // TODO: check if the requirement justify the use of asynchronous server socket channel
        try (final var serverChannel =
                     AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(SERVER_PORT))) {
            LOGGER.log(Level.INFO, "Server waiting for connections");
            waitingForConnections(serverChannel);
            while (!gamePlay.canBeStarted()) {
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
        // TODO: test this method under multiple connections
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                LOGGER.log(Level.INFO, "New connection accepted");
                requestForName(clientChannel);
                if (!gamePlay.canBeStarted()) {
                    serverChannel.accept(null, this);
                }
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Error in accepting connection", ex);
                if (!gamePlay.canBeStarted()) {
                    serverChannel.accept(null, this);
                }
            }
        });
    }

    private void requestForName(AsynchronousSocketChannel clientChannel) {
        final var byteBuffer = ByteBuffer.allocate(200);
        clientChannel.read(byteBuffer, READ_TIMEOUT, SECONDS, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer numBytes, Void attachment) {
                try {
                    final var name = byteBufferToString(byteBuffer, numBytes);
                    final var player = new Player(name, clientChannel);
                    gamePlay.addPlayer(player);
                    writeToClientChannel(clientChannel, OK_MESSAGE);
                } catch (RuntimeException ex) {
                    closeSocketChannel(clientChannel);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                LOGGER.log(Level.WARNING, "Timeout waiting for name", exc);
                closeSocketChannel(clientChannel);
            }
        });
    }

    public static String byteBufferToString(ByteBuffer byteBuffer, Integer numBytes) {
        final var byteArray = new byte[numBytes];
        byteBuffer.flip().get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private static void writeToClientChannel(AsynchronousSocketChannel channel, String message) {
        channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }

    private static void closeSocketChannel(AsynchronousSocketChannel channel) {
        try {
            channel.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in closing socket", ex);
        }
    }

}
