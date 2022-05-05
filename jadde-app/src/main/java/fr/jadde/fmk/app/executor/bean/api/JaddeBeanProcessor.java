package fr.jadde.fmk.app.executor.bean.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Allows to define a bean processor
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public interface JaddeBeanProcessor {

    /**
     * Sets the target application context.
     * The context should not be Injected from container
     *
     * @param context target application context
     */
    void setContext(final JaddeApplicationContext context);

    /**
     * Should process the target bean
     *
     * @param target target bean
     */
    default void process(final Object target, final Promise<Void> processPromise) {
        this.process(target);
        processPromise.tryComplete();
    }

    /**
     * Should process the target bean
     *
     * @param target target bean
     */
    void process(final Object target);

    /**
     * Does support target bean processing
     *
     * @param target the target bean which be processed
     * @return does support bean processing
     */
    boolean doesSupport(final Object target);


    int priorityOrder();


}
