package fit.apcs.magicalwheel.server.exception;

import java.io.Serial;

public class WrongMessageFormatException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5421539657629187651L;

    public WrongMessageFormatException(String message) {
        super(message);
    }

    public WrongMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongMessageFormatException(Throwable cause) {
        super(cause);
    }

}
