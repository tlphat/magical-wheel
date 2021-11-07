package fit.apcs.magicalwheel.lib.util;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

// TODO: add java doc for this utility class
public final class SocketWriteUtil {

    private SocketWriteUtil() {

    }

    public static void writeStringToChannel(AsynchronousSocketChannel channel, String message) {
        channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }

}
