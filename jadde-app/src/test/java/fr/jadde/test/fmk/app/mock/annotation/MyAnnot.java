package fr.jadde.test.fmk.app.mock.annotation;

import fr.jadde.fmk.container.annotation.JaddeBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Dorian GRELU
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JaddeBean
public @interface MyAnnot {
}
