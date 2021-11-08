package fit.apcs.magicalwheel.server.connection.handler;

import static fit.apcs.magicalwheel.lib.constant.EventType.JOIN_ROOM;
import static fit.apcs.magicalwheel.lib.constant.EventType.NEW_PLAYER;
import static fit.apcs.magicalwheel.lib.constant.EventType.fromString;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.FULL_CONNECTION;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.OK;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.SERVER_ERROR;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.USERNAME_EXISTED;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.USERNAME_INVALID;
import static fit.apcs.magicalwheel.lib.constant.StatusCode.WRONG_FORMAT;
import static fit.apcs.magicalwheel.lib.util.SocketWriteUtil.getMessageFromLines;
import static fit.apcs.magicalwheel.lib.util.SocketWriteUtil.writeStringToChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import fit.apcs.magicalwheel.lib.util.SocketReadUtil;
import fit.apcs.magicalwheel.server.connection.Server;
import fit.apcs.magicalwheel.server.entity.Player;
import fit.apcs.magicalwheel.server.exception.DuplicatedResourceException;
import fit.apcs.magicalwheel.server.exception.WrongMessageFormatException;
import fit.apcs.magicalwheel.server.gameplay.GamePlay;

public class JoinGameHandler implements CompletionHandler<Integer, Void> {

    private static final Logger LOGGER = Logger.getLogger(JoinGameHandler.class.getName());
    private final ByteBuffer byteBuffer;
    private final AsynchronousSocketChannel clientChannel;
    private final GamePlay gamePlay;

    public JoinGameHandler(ByteBuffer byteBuffer, AsynchronousSocketChannel clientChannel, GamePlay gamePlay) {
        this.byteBuffer = byteBuffer;
        this.clientChannel = clientChannel;
        this.gamePlay = gamePlay;
    }

    @Override
    public void completed(Integer numBytes, Void attachment) {
        try {
            final var reader = SocketReadUtil.byteBufferToReader(byteBuffer, numBytes);
            validateEventType(reader);
            final var username = reader.readLine().trim();
            final var players = addNewPlayer(username);
            announceNewPlayer(players, username);
        } catch (UnsupportedOperationException ex) { // TODO: simplify and generalize this logic
            writeStringToChannel(clientChannel,
                                 getMessageFromLines(JOIN_ROOM, FULL_CONNECTION.getCode()), false);
        } catch (DuplicatedResourceException ex) {
            writeStringToChannel(clientChannel,
                                 getMessageFromLines(JOIN_ROOM, USERNAME_EXISTED.getCode()), false);
        } catch (WrongMessageFormatException ex) {
            writeStringToChannel(clientChannel,
                                 getMessageFromLines(JOIN_ROOM, WRONG_FORMAT.getCode()), false);
        } catch (IllegalArgumentException ex) {
            writeStringToChannel(clientChannel,
                                 getMessageFromLines(JOIN_ROOM, USERNAME_INVALID.getCode()), false);
        } catch (IOException | RuntimeException ex) {
            writeStringToChannel(clientChannel,
                                 getMessageFromLines(JOIN_ROOM, SERVER_ERROR.getCode()), false);
        }
    }

    private static void validateEventType(BufferedReader reader) throws IOException {
        final var type = fromString(reader.readLine());
        if (type != JOIN_ROOM) {
            LOGGER.log(Level.WARNING, "Expect response of type {0}, got {1}",
                       new Object[]{ JOIN_ROOM, type});
            throw new WrongMessageFormatException("Wrong event type");
        }
    }

    private List<Player> addNewPlayer(String username) {
        final var player = new Player(username, clientChannel);
        final var players = gamePlay.addPlayer(player);
        final var responseBody =
                Stream.concat(Stream.of(OK.getCode(), GamePlay.MAX_NUM_PLAYERS, players.size()),
                              players.stream().map(Player::getUsername)).toArray(Object[]::new);
        writeStringToChannel(clientChannel, getMessageFromLines(JOIN_ROOM, responseBody));
        return players;
    }

    private static void announceNewPlayer(List<Player> players, String username) {
        players.forEach(player -> {
            if (!player.getUsername().equals(username)) {
                writeStringToChannel(player.getChannel(), getMessageFromLines(NEW_PLAYER, username));
            }
        });
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        LOGGER.log(Level.WARNING, "Timeout waiting for name", exc);
        Server.closeClientChannel(clientChannel);
    }

}
