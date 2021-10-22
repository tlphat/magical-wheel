package fit.apcs.magicalwheel.server.entity;

public class Question {

    private final String keyword;
    private final String description;

    public Question(String keyword, String description) {
        this.keyword = keyword;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return keyword.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return obj instanceof Question && ((Question) obj).keyword.equals(keyword);
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", keyword, description);
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

}
