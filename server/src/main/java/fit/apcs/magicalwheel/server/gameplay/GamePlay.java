package fit.apcs.magicalwheel.server.gameplay;

import static fit.apcs.magicalwheel.lib.constant.EventType.START_GAME;
import static fit.apcs.magicalwheel.lib.util.SocketWriteUtil.getMessageFromLines;
import static fit.apcs.magicalwheel.lib.util.SocketWriteUtil.writeStringToChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import fit.apcs.magicalwheel.server.entity.Player;
import fit.apcs.magicalwheel.server.entity.Question;
import fit.apcs.magicalwheel.server.exception.DuplicatedResourceException;

public class GamePlay {

    public static final int MAX_NUM_PLAYERS = 2;

    private final List<Player> players = new ArrayList<>();
    private final Question question = GameLoader.getInstance().getRandomQuestion();

    public synchronized boolean canStart() {
        return players.size() == MAX_NUM_PLAYERS;
    }

    public synchronized List<Player> addPlayer(Player player) {
        if (canStart()) {
            throw new UnsupportedOperationException("There is enough players got connected");
        }
        if (players.contains(player)) {
            throw new DuplicatedResourceException(
                    String.format("Player with username %s is already existed", player.getUsername()));
        }
        players.add(player);
        return players;
    }

    // There is only one thread expected to call this method. Synchronize is not necessary.
    public void start() {
        sendStartGameSignal();
    }

    private void sendStartGameSignal() {
        final var body = Stream.concat(Stream.of(question.getKeyword().length(),
                                                 question.getDescription(),
                                                 players.size()),
                                       players.stream().map(Player::getUsername)).toArray(Object[]::new);
        players.forEach(player -> writeStringToChannel(player.getChannel(),
                                                       getMessageFromLines(START_GAME, body)));
        // TODO: send start turn signal to first player AFTER the start game signal has arrived
    }

}
