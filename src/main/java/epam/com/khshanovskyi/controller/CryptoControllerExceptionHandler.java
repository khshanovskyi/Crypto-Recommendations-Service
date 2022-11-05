package epam.com.khshanovskyi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import epam.com.khshanovskyi.exception.CryptoNameDoesNotExistException;
import epam.com.khshanovskyi.exception.CryptoValuesNotPresentException;
import epam.com.khshanovskyi.exception.UnableToFindFileByPathException;
import epam.com.khshanovskyi.exception.UnableToFindFolderByPathException;

/**
 * Provides exception handling (catches exceptions and provides appropriate message and HTTP status) for the
 * {@link CryptoAdviceController}
 */
@ControllerAdvice(assignableTypes = {CryptoAdviceController.class})
public class CryptoControllerExceptionHandler {

    @ExceptionHandler(CryptoNameDoesNotExistException.class)
    public ResponseEntity<String> handleCryptoNameDoesNotExistException(CryptoNameDoesNotExistException ex) {
        return basicBodyForBadRequest(ex.getMessage());
    }

    @ExceptionHandler(CryptoValuesNotPresentException.class)
    public ResponseEntity<String> handleCryptoValuesNotPresentException(CryptoValuesNotPresentException ex) {
        return basicBodyForBadRequest(ex.getMessage());
    }

    @ExceptionHandler(UnableToFindFolderByPathException.class)
    public ResponseEntity<String> handleUnableToFindFolderByPathException(UnableToFindFolderByPathException ex) {
        return basicBodyForBadRequest(ex.getMessage());
    }

    @ExceptionHandler(UnableToFindFileByPathException.class)
    public ResponseEntity<String> handleUnableToFindFileByPathException(UnableToFindFileByPathException ex) {
        return basicBodyForBadRequest(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return basicBodyForBadRequest(ex.getMessage());
    }

    private ResponseEntity<String> basicBodyForBadRequest(String ex) {
        return ResponseEntity.badRequest().body(ex);
    }
}
