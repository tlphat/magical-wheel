package fit.apcs.magicalwheel.client;

import static fit.apcs.magicalwheel.lib.util.SocketReadUtil.getMessageFromLines;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fit.apcs.magicalwheel.lib.constant.EventType;

class SocketUtilTest {

    @Test
    void convertLinesToMessage() {
        final var message = getMessageFromLines(EventType.JOIN_ROOM, "john");
        assertEquals(EventType.JOIN_ROOM.getValue() + "\njohn\n", message);
    }

}
