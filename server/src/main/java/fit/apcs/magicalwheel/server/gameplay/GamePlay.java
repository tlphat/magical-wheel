package fit.apcs.magicalwheel.server.gameplay;

import java.util.ArrayList;
import java.util.List;

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
                    String.format("Player with username %s is already existed", player.getName()));
        }
        players.add(player);
        return players;
    }

    public void start() {
        // TODO: implement game logic
    }

}
