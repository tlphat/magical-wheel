package fit.apcs.magicalwheel.lib.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.lib.constant.EventType;

// TODO: add java doc for this utility class
public final class SocketWriteUtil {

    private static final Logger LOGGER = Logger.getLogger(SocketWriteUtil.class.getName());

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

    public static void writeStringToChannel(AsynchronousSocketChannel channel,
                                            String message, boolean keepAlive) {
        if (keepAlive) {
            writeStringToChannel(channel, message);
            return;
        }
        channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)), null,
                      new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {
                try {
                    channel.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error in closing connection", ex);
                }
            }

            @Override
            public void failed(Throwable ex, Void attachment) {
                LOGGER.log(Level.SEVERE, "Error in sending message", ex);
            }
        });
    }

}
