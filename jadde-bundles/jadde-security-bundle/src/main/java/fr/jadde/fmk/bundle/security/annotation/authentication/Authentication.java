package fr.jadde.fmk.bundle.security.annotation.authentication;

import fr.jadde.fmk.container.annotation.JaddeBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@JaddeBean
public @interface Authentication {

}
