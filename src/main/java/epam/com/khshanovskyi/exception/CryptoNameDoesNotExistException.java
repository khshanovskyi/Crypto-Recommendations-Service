package epam.com.khshanovskyi.exception;

public class CryptoNameDoesNotExistException extends RuntimeException {

    public CryptoNameDoesNotExistException(String message) {
        super(message);
    }
}
