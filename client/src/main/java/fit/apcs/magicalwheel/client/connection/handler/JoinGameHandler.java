package fit.apcs.magicalwheel.client.connection.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.model.Player;
import fit.apcs.magicalwheel.client.view.panel.WelcomePanel;
import fit.apcs.magicalwheel.lib.constant.EventType;
import fit.apcs.magicalwheel.lib.constant.StatusCode;
import fit.apcs.magicalwheel.lib.util.SocketReadUtil;

public class JoinGameHandler implements CompletionHandler<Integer, Void> {

    private static final Logger LOGGER = Logger.getLogger(JoinGameHandler.class.getName());

    private final ByteBuffer byteBuffer;
    private final WelcomePanel panel;
    private final Client client;

    public JoinGameHandler(ByteBuffer byteBuffer, WelcomePanel panel, Client client) {
        this.byteBuffer = byteBuffer;
        this.panel = panel;
        this.client = client;
    }

    @Override
    public void completed(Integer numBytes, Void attachment) {
        LOGGER.log(Level.INFO, "Response:\n{0}", SocketReadUtil.byteBufferToString(byteBuffer, numBytes));
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            verifyEventType(reader);
            verifyReturnCode(reader);
            joinWaitingRoom(reader);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error in parsing response", ex);
            panel.setMessage(StatusCode.WRONG_FORMAT.getMessage());
            client.closeConnection();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Response is not OK", ex);
            client.closeConnection();
        }
    }

    private static void verifyEventType(BufferedReader reader) throws IOException {
        final var type = EventType.fromString(reader.readLine());
        if (type != EventType.JOIN_ROOM) {
            LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                       new Object[]{EventType.JOIN_ROOM, type});
            throw new IllegalArgumentException("Wrong event type");
        }
    }

    private void verifyReturnCode(BufferedReader reader) throws IOException {
        final var statusCode = StatusCode.fromString(reader.readLine());
        if (statusCode != StatusCode.OK) {
            panel.setMessage(statusCode.getMessage());
            throw new IllegalStateException(statusCode.getMessage());
        }
    }

    private void joinWaitingRoom(BufferedReader reader) throws IOException {
        final var maxNumPlayers = Integer.parseInt(reader.readLine());
        final var curNumPlayers = Integer.parseInt(reader.readLine());
        final var listPlayers = new ArrayList<Player>();
        for (var order = 1; order <= curNumPlayers; ++order) {
            final var username = reader.readLine().trim();
            listPlayers.add(new Player(order, username));
        }
        panel.joinWaitingRoom(maxNumPlayers, listPlayers);
    }

    @Override
    public void failed(Throwable ex, Void attachment) {
        LOGGER.log(Level.WARNING, "Cannot get response from server", ex);
        client.closeConnection();
    }

}
