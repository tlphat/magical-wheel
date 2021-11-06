package fit.apcs.magicalwheel.client.view;

public class Player {

    private final String username;
    private final int order;

    public Player(String username, int order) {
        this.username = username;
        this.order = order;
    }

    public String getUsername() {
        return username;
    }

    public int getOrder() {
        return order;
    }

}
