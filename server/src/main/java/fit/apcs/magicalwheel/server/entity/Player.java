package fit.apcs.magicalwheel.server.entity;

public class Player {

    private final String name;
    private int point; // = 0 when first initialized

    public Player(String name) {
        this.name = name;
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
