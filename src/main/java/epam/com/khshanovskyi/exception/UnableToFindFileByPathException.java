package epam.com.khshanovskyi.exception;

public class UnableToFindFileByPathException extends RuntimeException {

    public UnableToFindFileByPathException(String message) {
        super(message);
    }

    public UnableToFindFileByPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
