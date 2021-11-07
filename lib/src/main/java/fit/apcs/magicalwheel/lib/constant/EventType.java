package fit.apcs.magicalwheel.lib.constant;

import java.util.stream.Stream;

import javax.annotation.Nullable;

public enum EventType {

    JOIN_ROOM("1"),
    NEW_PLAYER("2"),
    START_GAME("3"),
    PLAYER_GUESS("4"),
    START_TURN("5"),
    END_GAME("6");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Nullable
    public static EventType fromString(String str) {
        return Stream.of(values())
                     .filter(s -> s.getValue().equalsIgnoreCase(str))
                     .findFirst()
                     .orElseGet(null);
    }

}
