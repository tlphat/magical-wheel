package fit.apcs.magicalwheel.lib.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class SocketReadUtil {

    /**
     * Timeout in seconds for read operation of socket channel
     */
    public static final int TIMEOUT_IN_SECONDS = 5;

    private SocketReadUtil() {

    }

    /**
     * Convert byte buffer into a string.
     *
     * @param byteBuffer byte buffer to be converted
     * @param numBytes   number of bytes in this byte buffer
     * @return a string representing the content of the byte buffer in UTF-8
     */
    public static String byteBufferToString(ByteBuffer byteBuffer, Integer numBytes) {
        final var byteArray = new byte[numBytes];
        byteBuffer.flip().get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /**
     * Convert byte buffer into buffer reader. Caller can extract lines from the returned reader.
     *
     * @param byteBuffer byte buffer to be converted
     * @param numBytes   number of bytes in this byte buffer
     * @return a BufferedReader representing the content of the byte buffer
     */
    public static BufferedReader byteBufferToReader(ByteBuffer byteBuffer, Integer numBytes) {
        final var stringResult = byteBufferToString(byteBuffer, numBytes);
        final var stringReader = new StringReader(stringResult);
        return new BufferedReader(stringReader);
    }

}
