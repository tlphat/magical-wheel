package fit.apcs.magicalwheel.server.connection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ServerTest {

    private Thread serverThread;

    @BeforeEach
    void startServerThread() {
        serverThread = new Thread(() -> Server.getInstance().run());
        serverThread.start();
    }

    @AfterEach
    void waitForServerThread() throws InterruptedException {
        serverThread.join();
    }

    @Test
    @Disabled("This test might take some resources")
    void testMultipleConnections() {
        assertDoesNotThrow(() -> {
            final var socketList = IntStream.range(0, 100).mapToObj(i -> {
                try {
                    return SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
                } catch (IOException e) {
                    throw new RuntimeException("Cannot connect to server socket");
                }
            }).collect(Collectors.toList());
            for (int id = 0; id < 100; ++id) {
                final var channel = socketList.get(id);
                channel.write(ByteBuffer.wrap(String.valueOf(id).getBytes(StandardCharsets.UTF_8)));
            }
        });
    }

}
