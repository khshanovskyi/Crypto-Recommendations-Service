package epam.com.khshanovskyi.exception;

public class CryptoValuesNotPresentException extends RuntimeException {

    public CryptoValuesNotPresentException() {
    }

    public CryptoValuesNotPresentException(String message) {
        super(message);
    }
}
