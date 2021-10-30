package fit.apcs.magicalwheel.server.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SocketUtil {

    private static final Logger LOGGER = Logger.getLogger(SocketUtil.class.getName());

    private SocketUtil() {

    }

    public static String byteBufferToString(ByteBuffer byteBuffer, Integer numBytes) {
        final var byteArray = new byte[numBytes];
        byteBuffer.flip().get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    public static void writeStringToChannel(AsynchronousSocketChannel channel, String message) {
        channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }

    public static void closeSocketChannel(AsynchronousSocketChannel channel) {
        try {
            channel.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in closing socket", ex);
        }
    }

}
