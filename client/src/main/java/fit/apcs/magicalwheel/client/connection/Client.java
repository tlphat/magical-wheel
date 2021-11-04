package fit.apcs.magicalwheel.client.connection;

import static fit.apcs.magicalwheel.client.connection.SocketUtil.getMessageFromLines;
import static fit.apcs.magicalwheel.client.connection.SocketUtil.writeStringToChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final Client INSTANCE = new Client();

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static final int TIMEOUT = 10;  // in seconds

    private AsynchronousSocketChannel channel;

    private Client() {

    }

    public static Client getInstance() {
        return INSTANCE;
    }

    public void openConnection() throws IOException {
        channel = AsynchronousSocketChannel.open();
        channel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
    }

    public void closeConnection() throws IOException {
        channel.close();
    }

    public void sendUsername(String username) {
        // TODO: preprocess username (trim, uppercase, etc.) if necessary
        final var message = getMessageFromLines(1, username);
        writeStringToChannel(channel, message);
        waitForJoinGameResponse();
    }

    private void waitForJoinGameResponse() {
        final var byteBuffer = ByteBuffer.allocate(100);
        final var responseHandler = new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer numBytes, Void attachment) {
                LOGGER.log(Level.INFO, "Response: {0}", SocketUtil.byteBufferToString(byteBuffer, numBytes));
                // TODO: parse response message
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.WARNING, "Cannot get response from server", ex);
            }
        };
        channel.read(byteBuffer, TIMEOUT, TimeUnit.SECONDS, null, responseHandler);
    }

}
