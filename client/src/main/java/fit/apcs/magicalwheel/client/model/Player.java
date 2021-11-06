package fit.apcs.magicalwheel.client.model;

public class Player {

    private final String username;
    private final int order;
    private final int point;

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

}
