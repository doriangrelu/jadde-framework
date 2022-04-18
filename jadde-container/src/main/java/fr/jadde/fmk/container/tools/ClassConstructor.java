package fr.jadde.fmk.container.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Stream;

public class ClassConstructor {

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> createInstance(Class<T> className) {
        return Stream.of(className.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .map(constructor -> {
                    try {
                        return (T) constructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException(e);
                    }
                });

    }

}
