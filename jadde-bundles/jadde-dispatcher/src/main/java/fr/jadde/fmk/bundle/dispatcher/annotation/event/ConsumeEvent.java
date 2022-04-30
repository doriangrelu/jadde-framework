package fr.jadde.fmk.bundle.dispatcher.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConsumeEvent {

    String value();

    boolean remote() default false;

    boolean topic() default true;

}
