package fit.apcs.magicalwheel.client.connection;

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
import fit.apcs.magicalwheel.client.view.panel.WaitingPanel;
import fit.apcs.magicalwheel.client.view.panel.WelcomePanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;
import fit.apcs.magicalwheel.lib.util.SocketWriteUtil;

public final class Client {

    public static final int TIMEOUT = 10;  // in seconds

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
        final var message = SocketReadUtil.getMessageFromLines(EventType.JOIN_ROOM, username.trim());
        SocketWriteUtil.writeStringToChannel(channel, message);
        waitForJoinGameResponse(panel);
    }

    private void waitForJoinGameResponse(WelcomePanel panel) {
        final var byteBuffer = ByteBuffer.allocate(1000);
        final var responseHandler = new JoinGameHandler(byteBuffer, panel);
        channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, responseHandler);
    }

    public void waitForStartGameSignal(WaitingPanel panel) {
        final var byteBuffer = ByteBuffer.allocate(2000);
        final var responseHandler = new StartGameHandler(byteBuffer, panel, channel);
        channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, responseHandler);
    }

}
