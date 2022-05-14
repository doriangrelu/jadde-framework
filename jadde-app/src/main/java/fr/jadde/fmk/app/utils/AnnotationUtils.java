package fr.jadde.fmk.app.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AnnotationUtils {

    public static boolean isAnnotationPresent(final Class<?> className, Class<? extends Annotation> annotationName) {
        return isAnnotationPresent(className.getAnnotations(), annotationName);
    }


    public static boolean isAnnotationPresent(final Method method, Class<? extends Annotation> annotationName) {
        return isAnnotationPresent(method.getAnnotations(), annotationName);
    }

    public static <T extends Annotation> Optional<T> getAnnotation(final Method method, Class<T> annotationName) {
        return getAnnotation(method.getAnnotations(), annotationName);
    }

    public static <T extends Annotation> Optional<T> getAnnotation(final Class<?> className, Class<T> annotationName) {
        return getAnnotation(className.getAnnotations(), annotationName);
    }

    private static boolean isAnnotationPresent(final Annotation[] annotations, Class<? extends Annotation> annotationName) {
        return Stream.of(annotations).anyMatch(annotationMatcher(annotationName));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> Optional<T> getAnnotation(final Annotation[] annotations, Class<? extends Annotation> annotationName) {
        return (Optional<T>) Stream.of(annotations).filter(annotationMatcher(annotationName)).findFirst();
    }

    private static Predicate<Annotation> annotationMatcher(final Class<? extends Annotation> annotationName) {
        return annotation -> annotation.annotationType().isAnnotationPresent(annotationName) || annotation.annotationType().isAssignableFrom(annotationName);
    }

}
