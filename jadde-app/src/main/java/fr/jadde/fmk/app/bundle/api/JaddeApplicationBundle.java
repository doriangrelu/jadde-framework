package fr.jadde.fmk.app.bundle.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

public interface JaddeApplicationBundle {

    boolean next(final JaddeApplicationContext context);

    JaddeApplicationBundle setNext(final JaddeApplicationBundle middleware);

    String getName();

    default short priorityOrder() {
        return Short.MAX_VALUE;
    }

}
