package fit.apcs.magicalwheel.server.connection;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.lib.util.SocketReadUtil;
import fit.apcs.magicalwheel.server.entity.Player;
import fit.apcs.magicalwheel.server.gameplay.GamePlay;

public final class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final Server INSTANCE = new Server();

    private static final String OK_MESSAGE = "1\n0\n2\n";
    private static final int SERVER_PORT = 8080;

    private final GamePlay gamePlay = new GamePlay();

    private Server() {

    }

    public static Server getInstance() {
        return INSTANCE;
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
                readName(clientChannel);
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

    private void readName(AsynchronousSocketChannel clientChannel) {
        final var byteBuffer = ByteBuffer.allocate(200);
        clientChannel.read(byteBuffer, SocketReadUtil.TIMEOUT_IN_SECONDS, SECONDS, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer numBytes, Void attachment) {
                try {
                    final var name = SocketUtil.byteBufferToString(byteBuffer, numBytes);
                    final var player = new Player(name, clientChannel);
                    gamePlay.addPlayer(player);
                    SocketUtil.writeStringToChannel(clientChannel, OK_MESSAGE + name + '\n');
                } catch (RuntimeException ex) {
                    SocketUtil.closeSocketChannel(clientChannel);
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                LOGGER.log(Level.WARNING, "Timeout waiting for name", exc);
                SocketUtil.closeSocketChannel(clientChannel);
            }
        });
    }

}
