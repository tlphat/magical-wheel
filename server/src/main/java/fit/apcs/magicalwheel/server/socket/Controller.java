package fit.apcs.magicalwheel.server.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private static final int PORT = 8080;

    private final AtomicInteger numConnections = new AtomicInteger();

    public void initServerSocket() {
        // TODO: check if the requirement justify the use of asynchronous server socket channel
        try (final var serverChannel = AsynchronousServerSocketChannel.open()
                                                                      .bind(new InetSocketAddress(PORT))) {
            LOGGER.log(Level.INFO, "Server waiting for connections");
            waitingForConnections(serverChannel);
            while (numConnections.intValue() < 2) { // FIXME: extract this constant
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
            public void completed(AsynchronousSocketChannel result, Void attachment) {
                LOGGER.log(Level.INFO, "New connection accepted");
                if (numConnections.incrementAndGet() < 2) {
                    serverChannel.accept(null, this);
                }
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Error in accepting connection", ex);
                if (numConnections.incrementAndGet() < 2) {
                    serverChannel.accept(null, this);
                }
            }
        });
    }

}
