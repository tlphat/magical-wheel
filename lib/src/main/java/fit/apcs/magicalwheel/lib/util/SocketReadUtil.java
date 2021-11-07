package fit.apcs.magicalwheel.lib.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import fit.apcs.magicalwheel.lib.constant.EventType;

// TODO: add java doc for this utility class
public final class SocketReadUtil {

    private SocketReadUtil() {

    }

    public static String getMessageFromLines(EventType type, Object... lines) {
        final var strBuilder = new StringBuilder().append(type.getValue() + '\n');
        for (var line: lines) {
            strBuilder.append(line.toString() + '\n');
        }
        return strBuilder.toString();
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
