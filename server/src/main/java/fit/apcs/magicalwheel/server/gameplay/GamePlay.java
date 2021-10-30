package fit.apcs.magicalwheel.server.gameplay;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import fit.apcs.magicalwheel.server.entity.Player;
import fit.apcs.magicalwheel.server.entity.Question;

public class GamePlay {

    private static final int MAX_NUM_PLAYERS = 2;

    private final ConcurrentHashMap<Player, Integer> mapPlayerOrder = new ConcurrentHashMap<>();
    private final Question question = GameLoader.getInstance().getRandomQuestion();
    private final AtomicInteger currentNumPlayer = new AtomicInteger(0);

    public synchronized boolean canStart() {
        return mapPlayerOrder.size() == MAX_NUM_PLAYERS;
    }

    public synchronized void addPlayer(Player player) {
        if (canStart()) {
            throw new UnsupportedOperationException("There is enough players got connected");
        }
        if (mapPlayerOrder.containsKey(player)) {
            throw new IllegalArgumentException(
                    String.format("Player with username %s is already existed", player.getName()));
        }
        mapPlayerOrder.put(player, currentNumPlayer.incrementAndGet());
    }

    public void start() {
        // TODO: implement game logic
    }

}
