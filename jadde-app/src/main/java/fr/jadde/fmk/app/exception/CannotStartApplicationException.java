package fr.jadde.fmk.app.exception;

public class CannotStartApplicationException extends RuntimeException {
    
    public CannotStartApplicationException(String message) {
        super(message);
    }
}
