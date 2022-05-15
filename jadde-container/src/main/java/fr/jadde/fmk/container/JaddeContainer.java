package fr.jadde.fmk.container;

import fr.jadde.fmk.container.annotation.Default;
import fr.jadde.fmk.container.annotation.Qualifier;
import fr.jadde.fmk.container.exception.MissingEmptyConstructorException;
import fr.jadde.fmk.container.exception.NotSingleBeanException;


import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Allows you to provide a dependency injector container
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeContainer {

    private final Set<Object> instances;

    private final Lock reentrantLock;

    /**
     * Ctor.
     */
    public JaddeContainer() {
        this.instances = Collections.synchronizedSet(new LinkedHashSet<>());
        this.reentrantLock = new ReentrantLock();
    }

    /**
     * Creates and return created instance
     *
     * @param targetClass target class name
     * @param <T>         expected generic type
     * @return expected instance
     */
    public <T> T registerAndGetInstance(final Class<T> targetClass) {
        try {
            this.reentrantLock.lock();
            final T instance = this.createInstance(targetClass);
            return this.bindAndGetInstance(instance);
        } finally {
            this.reentrantLock.unlock();
        }
    }

    /**
     * Binds new instance in container
     *
     * @param instance target instance
     * @param <T>      target type
     * @return instance
     */
    public <T> T bindAndGetInstance(final T instance) {
        this.bindInstance(instance);
        return instance;
    }

    /**
     * Binds new instance in container
     *
     * @param instance target instance
     * @param <T>      target type
     */
    public <T> void bindInstance(final T instance) {
        this.instances.add(instance);
    }

    /**
     * Registers new instance in container (the class must have an empty constructor)
     *
     * @param targetClass target class
     * @param <T>         target expected generic type
     */
    public <T> void registerInstance(final Class<T> targetClass) {
        try {
            this.reentrantLock.lock();
            final T instance = this.createInstance(targetClass);
            this.bindInstance(instance);
        } finally {
            this.reentrantLock.unlock();
        }
    }

    /**
     * Tries to resolve bean from annotation
     *
     * @param annotations target annotations array
     * @param <T>         expected generic bean type
     * @return optional target bean
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> tryResolve(final Annotation... annotations) {
        return (Optional<T>) this.instances.stream()
                .filter(o -> Stream.of(annotations).allMatch(a -> o.getClass().isAnnotationPresent(a.getClass())))
                .findFirst();
    }

    /**
     * Resolves all beans from class name
     *
     * @param targetClassName the target class name
     * @param <T>             expected generic beans type
     * @return the list that contains all beans
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> resolveAll(final Class<T> targetClassName) {
        try {
            final List<T> result = new ArrayList<>();
            this.instances.forEach(object -> {
                if (targetClassName.isAssignableFrom(object.getClass())) {
                    result.add((T) object);
                }
            });
            return Collections.unmodifiableList(result);
        } catch (ClassCastException e) {
            return Collections.emptyList();
        }
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> resolveAll() {
        return (List<T>) List.copyOf(this.instances);
    }

    /**
     * Provides real instance resolving
     * The Jadde container return by default a proxy object
     * This method bypass the proxy object
     * In most cases you must use the proxy object
     *
     * @param targetClassName target class name
     * @param qualifier       target qualifier
     * @param <T>             expected generic beans type
     * @return optional real instance
     */
    public <T> Optional<T> resolve(final Class<T> targetClassName, final String qualifier) {
        final List<T> matchInstances = this.resolveAll(targetClassName);
        if (matchInstances.size() > 1) {
            return this.resolveConflict(targetClassName, matchInstances, qualifier);
        }
        return Optional.ofNullable(matchInstances.get(0));
    }

    private <T> Optional<T> resolveConflict(final Class<T> targetClassName, final List<T> conflictInstances, final String qualifier) {
        final List<T> defaultElements = conflictInstances.stream()
                .filter(targetClass -> targetClass.getClass().isAnnotationPresent(Default.class))
                .toList();

        final List<T> qualifiedElements = conflictInstances.stream()
                .filter(targetClass -> targetClass.getClass().isAnnotationPresent(Qualifier.class))
                .filter(targetClass -> {
                    Qualifier[] qualifiers = targetClass.getClass().getAnnotationsByType(Qualifier.class);
                    return qualifiers.length > 0 && qualifiers[0].value().equals(qualifier);
                })
                .toList();
        if (qualifiedElements.size() > 0) {
            if (qualifiedElements.size() > 1) {
                throw new NotSingleBeanException("Multiples beans for class name '" + targetClassName + "' annoted with Qualifier('" + qualifier + "') annotation");
            }
            return Optional.ofNullable(qualifiedElements.get(0));
        }

        if (defaultElements.size() > 0) {
            if (defaultElements.size() > 1) {
                throw new NotSingleBeanException("Multiples beans for class name '" + targetClassName + "' annoted with Default annotation");
            }
            return Optional.ofNullable(defaultElements.get(0));
        }

        throw new NotSingleBeanException("Multiples beans for class name '" + targetClassName + "'; Use Default or Qualifier");
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(final Class<T> targetClass) {
        final MissingEmptyConstructorException missingEmptyConstructorException = new MissingEmptyConstructorException("Missing an empty constructor for bean '" + targetClass + "'");
        try {
            final Constructor<T>[] constructors = (Constructor<T>[]) targetClass.getConstructors();
            return Arrays.stream(constructors)
                    .filter(constructor -> constructor.getParameterCount() == 0)
                    .filter(AccessibleObject::trySetAccessible)
                    .findFirst()
                    .map(constructor -> {
                        try {
                            return constructor.newInstance();
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalStateException("Unexpected instantiation error '" + targetClass + "'");
                        }
                    })
                    .orElseThrow(() -> missingEmptyConstructorException);
        } catch (ClassCastException exception) {
            throw missingEmptyConstructorException;
        }
    }

    /**
     * Provides real instance resolving
     * The Jadde container return by default a proxy object
     * This method bypass the proxy object
     * In most cases you must use the proxy object
     *
     * @param targetClassName target class name
     * @param <T>             expected generic beans type
     * @return optional real instance
     */
    public <T> Optional<T> resolve(final Class<T> targetClassName) {
        return this.resolve(targetClassName, null);
    }

}
