package fit.apcs.magicalwheel.client.model;

public class Player {

    private final String username;
    private final int order;
    private int point;

    public Player(int order, String username) {
        this.order = order;
        this.username = username;
        point = 0;
    }

    public int getOrder() {
        return order;
    }

    public String getUsername() {
        return username;
    }

    public int getPoint() {
        return point;
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

    public void setPoint(int score) {
        point = score;
    }

}
