package fr.jadde.fmk.app.executor.bundle.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

public interface JaddeBundle {

    boolean next(final JaddeApplicationContext context);

    JaddeBundle setNext(final JaddeBundle middleware);

    String getName();

    default short priorityOrder() {
        return Short.MAX_VALUE;
    }

}
