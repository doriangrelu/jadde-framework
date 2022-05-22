package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.exception.CannotStartApplicationException;
import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.container.annotation.JaddeBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
class JaddeDependencyInjector {

    private final JaddeApplicationContext context;

    public JaddeDependencyInjector(final JaddeApplicationContext context) {
        this.context = context;
    }

    public void process(final Object target) {
        Stream.of(target.getClass().getDeclaredFields())
                .filter(field -> AnnotationUtils.isAnnotationPresent(field, Inject.class))
                .forEach(field -> {
                    log.debug("Tries inject dependency for field '{}' in class '{}'", field.getName(), target.getClass().getName());
                    this.injectDependency(target, field);
                });
    }

    @SuppressWarnings("unchecked")
    private void injectDependency(final Object target, final Field field) {
        final boolean accessibleStatus = field.trySetAccessible();
        if (accessibleStatus) {
            final Class<Object> dependencyClassName = (Class<Object>) field.getType();
            this.doesDependencyCompatibleOrFail(target, dependencyClassName);

            if (List.class.isAssignableFrom(field.getType()) || Set.class.isAssignableFrom(field.getType())) {
                this.processListDependenciesInjection(target, field, dependencyClassName);
            } else {
                this.processSingleDependencyInjection(target, field, dependencyClassName);
            }
        } else {
            log.warn("Cannot process field injection for field {} in class {}", field.getName(), target.getClass().getName());
        }
    }

    private void doesDependencyCompatibleOrFail(final Object target, final Class<?> dependencyClassName) {
        if (dependencyClassName.equals(target.getClass())) {
            throw new IllegalStateException("Cannot inject bean of same type '" + target.getClass().getName() + "'");
        }
    }

    private void processSingleDependencyInjection(final Object target, final Field field, final Class<Object> dependencyClassName) {
        final Optional<Object> dependency;
        if (field.getType().isAssignableFrom(JaddeApplicationContext.class)) {
            dependency = Optional.ofNullable(this.context);
        } else {
            final String qualifierName = AnnotationUtils.getAnnotation(field, Inject.class).orElseThrow().qualifier();
            dependency = this.context.container().resolve(dependencyClassName, qualifierName.isEmpty() ? null : qualifierName);
        }

        dependency.ifPresentOrElse(resolvedDependency -> {
            try {
                field.set(target, resolvedDependency);
            } catch (final IllegalAccessException e) {
                log.warn("Cannot process field injection for field '{}' in class '{}' with dependency '{}'",
                        field.getName(), target.getClass().getName(), dependencyClassName.getName(), e
                );
            }
            field.setAccessible(false);
        }, () -> {
            log.warn("Cannot process field injection for field '{}' in class '{}' because missing dependency '{}' in container",
                    field.getName(), target.getClass().getName(), dependencyClassName.getName()
            );
            if (AnnotationUtils.getAnnotation(field, Inject.class).orElseThrow().required()) {
                throw new CannotStartApplicationException("Cannot start application because missing required dependency '" + dependencyClassName.getName() + "' " +
                        "for injection in '" + target.getClass() + "'");
            }
        });
    }

    private void processListDependenciesInjection(final Object target, final Field field, final Class<Object> dependencyClassName) {
        final ParameterizedType stringCollectionType = (ParameterizedType) field.getGenericType();
        if (stringCollectionType.getActualTypeArguments().length == 0) {
            throw new IllegalStateException("Cannot find the target bean type for '" + target.getClass().getName() + "' --> '" + field.getName() + "'");
        }
        final Class<?> targetDependencyListName = (Class<?>) stringCollectionType.getActualTypeArguments()[0];
        this.doesDependencyCompatibleOrFail(target, targetDependencyListName);

        Collection<?> dependencies = this.context.container().resolveAll(targetDependencyListName);
        if (Set.class.isAssignableFrom(field.getType())) {
            dependencies = new HashSet<>(dependencies);
        }
        try {
            field.set(target, dependencies);
        } catch (IllegalAccessException e) {
            log.warn("Cannot process field injection for field '{}' in class '{}' with dependency '{}'",
                    field.getName(), target.getClass().getName(), dependencyClassName.getName(), e
            );
        }
    }

    public boolean doesSupport(final Object target) {
        return null != target && AnnotationUtils.isAnnotationPresent(target.getClass(), JaddeBean.class);
    }

}
