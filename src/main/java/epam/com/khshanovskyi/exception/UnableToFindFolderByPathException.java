package epam.com.khshanovskyi.exception;

public class UnableToFindFolderByPathException extends RuntimeException {

    public UnableToFindFolderByPathException(String message) {
        super(message);
    }
}
