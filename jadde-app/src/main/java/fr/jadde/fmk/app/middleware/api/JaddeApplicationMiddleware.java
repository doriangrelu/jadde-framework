package fr.jadde.fmk.app.middleware.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

public interface JaddeApplicationMiddleware {

    boolean next(final JaddeApplicationContext context);

    JaddeApplicationMiddleware setNext(final JaddeApplicationMiddleware middleware);

}
