package fr.jadde.fmk.bundle.web.exceptions;

import java.lang.reflect.Method;

public class BadEndpointDeclarationException extends RuntimeException {

    public BadEndpointDeclarationException(final Class<?> targetClass, final Method targetMethod, final String message) {
        super(message + " >> " + targetClass.getName() + ':' + targetMethod.getName());
    }

}
