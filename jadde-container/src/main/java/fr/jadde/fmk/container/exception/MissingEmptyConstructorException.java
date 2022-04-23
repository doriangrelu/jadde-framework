package fr.jadde.fmk.container.exception;

public class MissingEmptyConstructorException extends RuntimeException {
    public MissingEmptyConstructorException(String message) {
        super(message);
    }
}
