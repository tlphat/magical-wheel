package fit.apcs.magicalwheel.server.entity;

import java.nio.channels.AsynchronousSocketChannel;

public class Player {

    private final String name;
    private final AsynchronousSocketChannel socketChannel;
    private int point = 0;

    public Player(String name, AsynchronousSocketChannel socketChannel) {
        this.name = name; // TODO: validate player name
        this.socketChannel = socketChannel;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return obj instanceof Player && ((Player) obj).name.equals(name);
    }

    @Override
    public String toString() {
        return String.format("{%s, %d}", name, point);
    }

    public String getName() {
        return name;
    }

    public int getPoint() {
        return point;
    }

    public void increasePoint(int increasedBy) {
        point += increasedBy;
    }

}
