package fit.apcs.magicalwheel.server.socket;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.server.gameplay.GamePlay;

public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private static final int SERVER_PORT = 8080;
    private static final long READ_TIMEOUT = 10; // in seconds

    private final GamePlay gamePlay = new GamePlay();

    public void runServer() {
        // TODO: check if the requirement justify the use of asynchronous server socket channel
        try (final var serverChannel =
                     AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(SERVER_PORT))) {
            LOGGER.log(Level.INFO, "Server waiting for connections");
            waitingForConnections(serverChannel);
            while (gamePlay.cannotBeStarted()) {
                Thread.onSpinWait();
            }
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
                requestForName(clientChannel);
                if (gamePlay.cannotBeStarted()) {
                    serverChannel.accept(null, this);
                }
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Error in accepting connection", ex);
                if (gamePlay.cannotBeStarted()) {
                    serverChannel.accept(null, this);
                }
            }
        });
    }

    private static void requestForName(AsynchronousSocketChannel clientChannel) {
        final var byteBuffer = ByteBuffer.allocate(200);
        clientChannel.read(byteBuffer, READ_TIMEOUT, SECONDS, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                LOGGER.log(Level.INFO, result.toString());
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                LOGGER.log(Level.WARNING, "Timeout waiting for name", exc);
                try {
                    clientChannel.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error in closing client connection", ex);
                }
            }
        });
    }

}
