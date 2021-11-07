package fit.apcs.magicalwheel.lib;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class SocketUtilTest {

    @Test
    void doNothingTest() {
        assertFalse(new SocketUtil().doNothing());
    }

}
