package fit.apcs.magicalwheel.lib.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

// TODO: add java doc for this utility class
public final class SocketReadUtil {

    /**
     * Timeout in seconds for read operation of socket channel
     */
    public static final int TIMEOUT_IN_SECONDS = 5;

    private SocketReadUtil() {

    }

    public static String byteBufferToString(ByteBuffer byteBuffer, Integer numBytes) {
        final var byteArray = new byte[numBytes];
        byteBuffer.flip().get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    public static BufferedReader byteBufferToReader(ByteBuffer byteBuffer, Integer numBytes) {
        final var stringResult = byteBufferToString(byteBuffer, numBytes);
        final var stringReader = new StringReader(stringResult);
        return new BufferedReader(stringReader);
    }

}