package fit.apcs.magicalwheel.lib.constant;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Use to represent status code of a request from client.
 * Its code should be attached to the response right after the EventType field.
 */
public enum StatusCode {

    OK(0, "OK"),
    USERNAME_EXISTED(1, "Username has already existed"),
    USERNAME_INVALID(2, "Username is invalid"),
    FULL_CONNECTION(3, "Server has received enough connection"),
    SERVER_ERROR(4, "Internal server error"),
    WRONG_FORMAT(5, "The message is in wrong format");

    private static final Logger LOGGER = Logger.getLogger(StatusCode.class.getName());
    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static StatusCode fromString(String str) {
        return Stream.of(values())
                     .filter(s -> String.valueOf(s.getCode()).equalsIgnoreCase(str))
                     .findFirst()
                     .orElseGet(() -> {
                         LOGGER.log(Level.WARNING, "Cannot map {0} to EventType", str);
                         return WRONG_FORMAT;
                     });
    }

}
