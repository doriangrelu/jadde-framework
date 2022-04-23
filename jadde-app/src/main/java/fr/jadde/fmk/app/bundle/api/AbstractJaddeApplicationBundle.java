package fr.jadde.fmk.app.bundle.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

/**
 * Provides middleware helper method
 *
 * @author Dorian GRELU
 */
public abstract class AbstractJaddeApplicationBundle implements JaddeApplicationBundle {

    private static final boolean DEFAULT_VALUE = true;
    private JaddeApplicationBundle nextMiddleware;


    @Override
    public JaddeApplicationBundle setNext(JaddeApplicationBundle middleware) {
        this.nextMiddleware = middleware;
        return middleware;
    }

    protected boolean handleNext(final JaddeApplicationContext context, final boolean defaultValue) {
        if (null == this.nextMiddleware) {
            return defaultValue;
        }
        return this.nextMiddleware.next(context);
    }

    protected boolean handleNext(final JaddeApplicationContext context) {
        return this.handleNext(context, DEFAULT_VALUE);
    }

}
