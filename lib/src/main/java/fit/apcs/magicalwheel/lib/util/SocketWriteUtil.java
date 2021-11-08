package fit.apcs.magicalwheel.lib.util;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

import fit.apcs.magicalwheel.lib.constant.EventType;

// TODO: add java doc for this utility class
public final class SocketWriteUtil {

    private SocketWriteUtil() {

    }

    public static String getMessageFromLines(EventType type, Object... lines) {
        final var strBuilder = new StringBuilder().append(type.getValue() + '\n');
        for (var line: lines) {
            strBuilder.append(line.toString() + '\n');
        }
        return strBuilder.toString();
    }

    public static void writeStringToChannel(AsynchronousSocketChannel channel, String message) {
        channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }

}
