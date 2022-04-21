package fr.jadde.test.fmk.app.mock.annotation;

import fr.jadde.fmk.app.assembly.processor.annotation.RootJaddeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RootJaddeAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnot {
}
