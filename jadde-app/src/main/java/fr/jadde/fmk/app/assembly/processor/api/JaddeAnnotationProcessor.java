package fr.jadde.fmk.app.assembly.processor.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

import java.lang.annotation.Annotation;

/**
 * Allows to define a bean processor
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public interface JaddeAnnotationProcessor {

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
    void process(final Object target);

    /**
     * Does support target bean processing
     *
     * @param target the target bean which be processed
     * @return does support bean processing
     */
    boolean doesSupport(final Object target);

}
