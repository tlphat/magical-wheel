package fit.apcs.magicalwheel.client.connection.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import fit.apcs.magicalwheel.client.view.panel.GamePanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;

public class StartTurnHandler implements CompletionHandler<Integer, Void> {

    private static final Logger LOGGER = Logger.getLogger(StartTurnHandler.class.getName());

    private final ByteBuffer byteBuffer;
    private final GamePanel panel;
    private final AsynchronousSocketChannel channel;

    public StartTurnHandler(ByteBuffer byteBuffer, GamePanel panel, AsynchronousSocketChannel channel) {
        this.byteBuffer = byteBuffer;
        this.panel = panel;
        this.channel = channel;
    }

    @Override
    public void completed(Integer numBytes, Void attachment) {
        LOGGER.log(Level.INFO, "Response:\n{0}", SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            final var eventType = validateEventType(reader);
            if (eventType == EventType.LEAVE_GAME) {
                handleLeaveGameSignal(reader);
                return;
            }
            final var username = reader.readLine().trim();
            final var turn = Integer.parseInt(reader.readLine());
            SwingUtilities.invokeLater(() -> panel.startTurn(username, turn));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
        }
    }

    private EventType validateEventType(BufferedReader reader) throws IOException {
        final var type = EventType.fromString(reader.readLine());
        if (type != EventType.START_TURN && type != EventType.LEAVE_GAME) {
            LOGGER.log(Level.WARNING, "Expect response of type {0} or {1}, got {2}",
                       new Object[]{EventType.START_TURN, EventType.LEAVE_GAME, type});
            clearAndReadBuffer();
            throw new IOException("Event type is not correct");
        }
        return type;
    }

    private void handleLeaveGameSignal(BufferedReader reader) throws IOException {
        final var usernameWhoLeave = reader.readLine().trim();
        SwingUtilities.invokeLater(() -> panel.eliminatePlayer(usernameWhoLeave));
        clearAndReadBuffer();
    }

    private void clearAndReadBuffer() {
        byteBuffer.clear();
        channel.read(byteBuffer, null, this);
    }

    @Override
    public void failed(Throwable ex, Void attachment) {
        LOGGER.log(Level.WARNING, "Cannot get response from server", ex);
    }

}
