package fit.apcs.magicalwheel.lib.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.lib.constant.EventType;

public final class SocketWriteUtil {

    private static final Logger LOGGER = Logger.getLogger(SocketWriteUtil.class.getName());
    private static final
        Map<AsynchronousSocketChannel, Queue<String>> CHANNELS_BUFFER = new ConcurrentHashMap<>();

    private SocketWriteUtil() {

    }

    /**
     * Get the string message consisting of lines of strings.
     * The first line shall be the event type. It will attach '\n' to the end of each line.
     *
     * @param type  EventType of the message
     * @param lines array of lines of strings
     * @return the string representing the message
     */
    public static String getMessageFromLines(EventType type, Object... lines) {
        final var strBuilder = new StringBuilder().append(type.getValue() + '\n');
        for (var line: lines) {
            strBuilder.append(line.toString() + '\n');
        }
        return strBuilder.toString();
    }

    /**
     * Write the message to channel socket. It shall continue sending messages until the buffer is empty.
     * When the writing collides, it would temporarily save the message into the buffer waiting for
     * the pending write finished.
     *
     * @param channel socket channel to send message
     * @param message the message to be sent
     */
    public static synchronized void writeStringToChannel(AsynchronousSocketChannel channel, String message) {
        try {
            channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)), null,
                          new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    writeMessageFromBuffer();
                }

                private void writeMessageFromBuffer() {
                    synchronized (SocketWriteUtil.class) {
                        final var buffer = CHANNELS_BUFFER.get(channel);
                        if (buffer != null && !buffer.isEmpty()) {
                            final var nextMessage = buffer.poll();
                            writeStringToChannel(channel, nextMessage);
                        }
                    }
                }

                @Override
                public void failed(Throwable ex, Void attachment) {
                    LOGGER.log(Level.SEVERE, "Error in sending message", ex);
                }
            });
        } catch (WritePendingException ex) {
            saveMessageToBuffer(channel, message);
        }
    }

    private static synchronized void saveMessageToBuffer(AsynchronousSocketChannel channel, String message) {
        LOGGER.log(Level.WARNING, "Write to this channel is pending, temporarily save to the buffer");
        CHANNELS_BUFFER.computeIfAbsent(channel, socketChannel -> new ConcurrentLinkedQueue<>());
        CHANNELS_BUFFER.get(channel).add(message);
    }

    /**
     * Same as {@link #writeStringToChannel(AsynchronousSocketChannel, String)}.
     * However, there is one more flag to indicate whether or not the connection should tear down after write.
     *
     * @param channel   socket channel to send message
     * @param message   the message to be sent
     * @param keepAlive when false, the connection will be torn down after the write
     */
    public static synchronized void writeStringToChannel(AsynchronousSocketChannel channel,
                                                         String message, boolean keepAlive) {
        if (keepAlive) {
            writeStringToChannel(channel, message);
            return;
        }
        try {
            channel.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)), null,
                          new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    synchronized (SocketWriteUtil.class) {
                        try {
                            final var buffer = CHANNELS_BUFFER.get(channel);
                            if (buffer != null && !buffer.isEmpty()) {
                                final var nextMessage = buffer.poll();
                                writeStringToChannel(channel, nextMessage);
                            } else {
                                channel.close();
                            }
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, "Error in closing connection", ex);
                        }
                    }
                }

                @Override
                public void failed(Throwable ex, Void attachment) {
                    LOGGER.log(Level.SEVERE, "Error in sending message", ex);
                }
            });
        } catch (WritePendingException ex) {
            saveMessageToBuffer(channel, message);
        }
    }

}
