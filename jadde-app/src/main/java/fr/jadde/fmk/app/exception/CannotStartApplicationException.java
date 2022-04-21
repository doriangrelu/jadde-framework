package fr.jadde.fmk.app.exception;

/**
 * Is lifted when the application can't start
 *
 * @author Dorian GRELU
 */
public class CannotStartApplicationException extends RuntimeException {

    public CannotStartApplicationException(String message) {
        super(message);
    }
}
