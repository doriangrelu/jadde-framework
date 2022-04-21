package fr.jadde.fmk.app.assembly.processor.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

/**
 * Allows to define a bean processor
 * Provide context management capabilities
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public abstract class AbstractJaddeAnnotationProcessor implements JaddeAnnotationProcessor {

    private JaddeApplicationContext context;

    @Override
    public void setContext(JaddeApplicationContext context) {
        this.context = context;
    }

    /**
     * Provides application context
     *
     * @return target application context
     */
    protected JaddeApplicationContext context() {
        return this.context;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
