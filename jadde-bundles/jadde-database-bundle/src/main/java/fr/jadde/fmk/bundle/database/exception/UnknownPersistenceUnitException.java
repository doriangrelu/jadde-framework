package fr.jadde.fmk.bundle.database.exception;

public class UnknownPersistenceUnitException extends RuntimeException {

    public UnknownPersistenceUnitException(String name) {
        super("Missing '" + name + "' persistence unit");
    }

}
