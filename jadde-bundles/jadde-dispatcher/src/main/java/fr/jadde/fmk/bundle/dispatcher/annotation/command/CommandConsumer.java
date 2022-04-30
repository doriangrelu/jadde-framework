package fr.jadde.fmk.bundle.dispatcher.annotation.command;

import fr.jadde.fmk.container.annotation.JaddeBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JaddeBean
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandConsumer {

}
