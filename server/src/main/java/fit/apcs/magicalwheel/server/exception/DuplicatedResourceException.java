package fit.apcs.magicalwheel.server.exception;

import java.io.Serial;

public class DuplicatedResourceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5183040827860164221L;

    public DuplicatedResourceException(String message) {
        super(message);
    }

    public DuplicatedResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedResourceException(Throwable cause) {
        super(cause);
    }

}
