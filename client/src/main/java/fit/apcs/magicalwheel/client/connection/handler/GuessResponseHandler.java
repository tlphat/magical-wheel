package fit.apcs.magicalwheel.client.connection.handler;

import static fit.apcs.magicalwheel.lib.constant.EventType.PLAYER_GUESS;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.view.panel.GamePanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;

public class GuessResponseHandler implements CompletionHandler<Integer, Void> {

    private static final Logger LOGGER = Logger.getLogger(GuessResponseHandler.class.getName());

    private final ByteBuffer byteBuffer;
    private final GamePanel panel;
    private final AsynchronousSocketChannel channel;

    public GuessResponseHandler(ByteBuffer byteBuffer, GamePanel panel, AsynchronousSocketChannel channel) {
        this.byteBuffer = byteBuffer;
        this.panel = panel;
        this.channel = channel;
    }

    @Override
    @SuppressWarnings("unused")
    public void completed(Integer numBytes, Void attachment) {
        LOGGER.log(Level.INFO, "Response:\n{0}", SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            validateEventType(reader);
            final var username = reader.readLine().trim();
            final var guessChar = reader.readLine();
            final var guessKeyword = reader.readLine();
            final var keyword = reader.readLine();
            final var score = Integer.parseInt(reader.readLine());
            final var isCorrectKeyWord = Integer.parseInt(reader.readLine()) != 0;
            final var isEnd = Integer.parseInt(reader.readLine()) != 0;
            stopTimer();
            updateMessage(username, guessChar, guessKeyword);
            updateScoreAndKeyword(username, keyword, score);
            handleGuessKeywordSignal(username, guessKeyword, isCorrectKeyWord);
            directToEndGameOrNewTurn(isEnd);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
        }
    }

    private void stopTimer() {
        panel.cancelTimer();
    }

    private void directToEndGameOrNewTurn(boolean isEnd) {
        if (isEnd) {
            Client.getInstance().listenToEndGameSignal(panel);
        } else {
            Client.getInstance().listenToStartTurnSignal(panel);
        }
    }

    private void updateMessage(String username, String guessChar, String guessKeyword) {
        panel.updateMessage(username, guessChar, guessKeyword);
    }

    private void updateScoreAndKeyword(String username, String keyword, int score) {
        panel.updateScore(username, score);
        panel.updateKeyword(keyword);
    }

    private void handleGuessKeywordSignal(String username, String guessKeyword, boolean isCorrectKeyWord) {
        if (!guessKeyword.isEmpty()) {
            if (isCorrectKeyWord) {
                panel.keywordGotGuessed();
            } else {
                panel.eliminatePlayer(username);
            }
        }
    }

    private void validateEventType(BufferedReader reader) throws IOException {
        final var type = EventType.fromString(reader.readLine());
        if (type != PLAYER_GUESS) {
            LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                       new Object[]{ PLAYER_GUESS, type});
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
