package fit.apcs.magicalwheel.client.connection.handler;

import static fit.apcs.magicalwheel.lib.constant.EventType.END_GAME;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import fit.apcs.magicalwheel.client.view.panel.GamePanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;

public class EndGameHandler implements CompletionHandler<Integer, Void> {

    private static final Logger LOGGER = Logger.getLogger(EndGameHandler.class.getName());

    private final ByteBuffer byteBuffer;
    private final GamePanel panel;
    private final AsynchronousSocketChannel channel;

    public EndGameHandler(ByteBuffer byteBuffer, GamePanel panel, AsynchronousSocketChannel channel) {
        this.byteBuffer = byteBuffer;
        this.panel = panel;
        this.channel = channel;
    }

    @Override
    public void completed(Integer numBytes, Void attachment) {
        LOGGER.log(Level.INFO, "Response:\n{0}", SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            validateEventType(reader);
            final var isCompletedKeyword = Integer.parseInt(reader.readLine()) != 0;
            final var winner = reader.readLine();
            final var keyword = reader.readLine();
            final var numPlayers = Integer.parseInt(reader.readLine());
            final var listScore = new ArrayList<Integer>();
            for (var order = 1; order <= numPlayers; ++order) {
                listScore.add(Integer.parseInt(reader.readLine()));
            }
            SwingUtilities.invokeLater(
                    () -> panel.switchToFinishGame(isCompletedKeyword, winner, keyword, numPlayers, listScore));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
        }
    }

    private void validateEventType(BufferedReader reader) throws IOException {
        final var type = EventType.fromString(reader.readLine());
        if (type != END_GAME) {
            LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                       new Object[]{ END_GAME, type});
            clearAndReadBuffer();
            throw new IOException("Event type is not correct");
        }
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
