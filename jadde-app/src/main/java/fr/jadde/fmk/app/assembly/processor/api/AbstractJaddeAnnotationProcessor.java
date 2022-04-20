package fr.jadde.fmk.app.assembly.processor.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

public abstract class AbstractJaddeAnnotationProcessor implements JaddeAnnotationProcessor {

    private JaddeApplicationContext context;

    @Override
    public void setContext(JaddeApplicationContext context) {
        this.context = context;
    }

    protected JaddeApplicationContext context() {
        return this.context;
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
