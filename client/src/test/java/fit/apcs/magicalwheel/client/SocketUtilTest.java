package fit.apcs.magicalwheel.client;

import static fit.apcs.magicalwheel.client.connection.SocketUtil.getMessageFromLines;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SocketUtilTest {

    @Test
    void convertLinesToMessage() {
        final var eventType = 1;
        final var username = "john";
        final var message = getMessageFromLines(eventType, username);
        assertEquals("1\njohn\n", message);
    }

}
