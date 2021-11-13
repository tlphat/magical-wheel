package fit.apcs.magicalwheel.lib.constant;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.annotation.Nullable;

/**
 * Use to specify the event type of the request/response.
 * Its <strong>value</strong> should be:
 * <ul>
 *     <li>attached at the beginning of any request/response from the sender</li>
 *     <li>checked by the receiver upon receiving the message</li>
 * </ul>
 */
// TODO: Fix event type number
public enum EventType {

    JOIN_ROOM("1"),
    NEW_PLAYER("2"),
    START_GAME("3"),
    PLAYER_GUESS("4"),
    START_TURN("5"),
    END_GAME("6");

    private static final Logger LOGGER = Logger.getLogger(EventType.class.getName());
    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Nullable
    @SuppressWarnings("ReturnOfNull")
    public static EventType fromString(String str) {
        return Stream.of(values())
                     .filter(s -> s.getValue().equalsIgnoreCase(str))
                     .findFirst()
                     .orElseGet(() -> {
                         LOGGER.log(Level.WARNING, "Cannot map {0} to EventType", str);
                         return null;
                     });
    }

}
