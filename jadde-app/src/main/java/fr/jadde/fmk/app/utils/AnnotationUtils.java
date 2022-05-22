package fr.jadde.fmk.app.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AnnotationUtils {

    public static boolean isAnnotationPresent(final Class<?> className, final Class<? extends Annotation> annotationName) {
        final Annotation[] annotations = className.getAnnotations();
        return isAnnotationPresent(annotations, annotationName);
    }

    public static boolean isAnnotationPresent(final Parameter parameter, final Class<? extends Annotation> annotationName) {
        final Annotation[] annotations = parameter.getAnnotations();
        return isAnnotationPresent(annotations, annotationName);
    }

    public static boolean isAnnotationPresent(final Field field, final Class<? extends Annotation> annotationName) {
        final Annotation[] annotations = field.getAnnotations();
        return isAnnotationPresent(annotations, annotationName);
    }

    public static boolean isAnnotationPresent(final Method method, final Class<? extends Annotation> annotationName) {
        final Annotation[] annotations = method.getAnnotations();
        return isAnnotationPresent(annotations, annotationName);
    }

    public static <T extends Annotation> Optional<T> getAnnotation(final Method method, final Class<T> annotationName) {
        final Annotation[] annotations = method.getAnnotations();
        return getAnnotation(annotations, annotationName);
    }

    public static <T extends Annotation> Optional<T> getAnnotation(final Field field, final Class<T> annotationName) {
        final Annotation[] annotations = field.getAnnotations();
        return getAnnotation(annotations, annotationName);
    }

    public static <T extends Annotation> Optional<T> getAnnotation(final Class<?> className, final Class<T> annotationName) {
        final Annotation[] annotations = className.getAnnotations();
        return getAnnotation(annotations, annotationName);
    }

    private static boolean isAnnotationPresent(final Annotation[] annotations, final Class<? extends Annotation> annotationName) {
        final Predicate<Annotation> predicate = annotationMatcher(annotationName);
        return Stream.of(annotations).anyMatch(predicate);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> Optional<T> getAnnotation(final Annotation[] annotations, final Class<? extends Annotation> annotationName) {
        final Predicate<Annotation> predicate = annotationMatcher(annotationName);
        return (Optional<T>) Stream.of(annotations).filter(predicate).findFirst();
    }

    private static Predicate<Annotation> annotationMatcher(final Class<? extends Annotation> annotationName) {
        return annotation -> {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            return annotationType.isAnnotationPresent(annotationName) || annotationType.isAssignableFrom(annotationName);
        };
    }

}
