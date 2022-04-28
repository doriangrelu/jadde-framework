package fr.jadde.fmk.app.context.tools;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

public class ClasspathResolver {

    private final Reflections reflections;

    private ClasspathResolver(final Reflections reflections) {
        this.reflections = reflections;
    }

    public Set<Class<?>> resolveByAnnotation(final Class<? extends Annotation> targetAnnotation) {
        return this.reflections.getTypesAnnotatedWith(targetAnnotation)
                .stream()
                .filter(aClass -> !aClass.isAnnotation())
                .filter(aClass -> !Modifier.isAbstract(aClass.getModifiers()))
                .filter(aClass -> !Modifier.isInterface(aClass.getModifiers()))
                .collect(Collectors.toSet());
    }

    public <T> Set<Class<? extends T>> resolveBySubtype(final Class<T> targetAnnotation) {
        return this.reflections.getSubTypesOf(targetAnnotation)
                .stream()
                .filter(aClass -> !Modifier.isAbstract(aClass.getModifiers()))
                .filter(aClass -> !Modifier.isInterface(aClass.getModifiers()))
                .collect(Collectors.toSet());
    }

    public static ClasspathResolver create() {
        final ConfigurationBuilder configuration = new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath());
        final Reflections reflections = new Reflections(configuration);
        return new ClasspathResolver(reflections);
    }

}
