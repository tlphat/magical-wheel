package fit.apcs.magicalwheel.client.view;

public class Player {
    private String username;
    private int order;

    public Player(String username, int order) {
        this.username = username;
        this.order = order;
    }

    public String getUsername() {
        return this.username;
    }

    public int getOrder() {
        return this.order;
    }
}