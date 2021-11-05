package fit.apcs.magicalwheel.client.connection;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

public final class SocketUtil {

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

    public static String getMessageFromLines(Object... lines) {
        final var strBuilder = new StringBuilder();
        for (var line: lines) {
            strBuilder.append(line.toString() + '\n');
        }
        return strBuilder.toString();
    }

}
