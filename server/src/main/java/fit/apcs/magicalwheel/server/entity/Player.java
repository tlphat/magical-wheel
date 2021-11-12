package fit.apcs.magicalwheel.server.entity;

import java.nio.channels.AsynchronousSocketChannel;

public class Player {

    private final String username;
    private final AsynchronousSocketChannel socketChannel;
    private int point = 0;

    public Player(String username, AsynchronousSocketChannel socketChannel) {
        this.username = username; // TODO: validate player name
        this.socketChannel = socketChannel;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return obj instanceof Player && ((Player) obj).username.equals(username);
    }

    @Override
    public String toString() {
        return String.format("{%s, %d}", username, point);
    }

    public String getUsername() {
        return username;
    }

    public AsynchronousSocketChannel getChannel() {
        return socketChannel;
    }

    public int getPoint() {
        return point;
    }

    public void increasePoint(int increasedBy) {
        point += increasedBy;
    }

}
