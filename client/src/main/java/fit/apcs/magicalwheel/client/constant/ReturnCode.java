package fit.apcs.magicalwheel.client.constant;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public enum ReturnCode {

    OK(0, "OK"),
    USERNAME_EXISTED(1, "Username has already existed"),
    USERNAME_INVALID(2, "Username is invalid"),
    SERVER_ERROR(3, "Internal server error"),
    WRONG_FORMAT(4, " ");

    private static final Logger LOGGER = Logger.getLogger(ReturnCode.class.getName());
    private final int code;
    private final String message;

    ReturnCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ReturnCode fromString(String str) {
        return Stream.of(values())
                     .filter(s -> String.valueOf(s.getCode()).equalsIgnoreCase(str))
                     .findFirst()
                     .orElseGet(() -> {
                         LOGGER.log(Level.WARNING, "Cannot map {0} to EventType", str);
                         return WRONG_FORMAT;
                     });
    }

}
