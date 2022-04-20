package fr.jadde.fmk.app.assembly.processor.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

import java.lang.annotation.Annotation;

public interface JaddeAnnotationProcessor {

    void setContext(final JaddeApplicationContext context);

    void process(final Annotation annotation, final Object target);

    boolean doesSupport(final Class<? extends Annotation> annotation, final Object target);

}
