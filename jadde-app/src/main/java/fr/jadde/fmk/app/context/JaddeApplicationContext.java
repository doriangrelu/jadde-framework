package fr.jadde.fmk.app.context;

import fr.jadde.fmk.app.JaddeApplication;
import fr.jadde.fmk.app.assembly.resolver.ClasspathResolver;
import fr.jadde.fmk.app.exception.ImmutableContextViolationException;
import fr.jadde.fmk.container.JaddeContainer;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents the current application context
 * It's provide VertX instance, parsed arguments list and current application FQN
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationContext.class);

    public static final Pattern ARGUMENT_PATTERN = Pattern
            .compile("^([a-z]+([a-z\\-\\.][a-z]+)*)=([a-z0-9]+([a-z\\-\\.][a-z0-9]+)*)$");

    private ClasspathResolver classpathResolver;

    private Class<?> applicationClassName;

    private Vertx vertx;

    private JaddeContainer container;

    private Map<String, String> arguments;

    private AtomicBoolean isBuild = new AtomicBoolean(false);

    public JaddeApplicationContext() {
        this.arguments = new ConcurrentHashMap<>();
    }

    public JaddeApplicationContext withContainer(final JaddeContainer container) {
        this.checkBuildIntegrityOrFail();
        this.container = container;
        return this;
    }

    public JaddeApplicationContext withVertX(final Vertx vertx) {
        this.checkBuildIntegrityOrFail();
        this.vertx = vertx;
        return this;
    }

    public JaddeApplicationContext withApplicationClassName(final Class<? extends JaddeApplication> applicationClassName) {
        this.checkBuildIntegrityOrFail();
        this.applicationClassName = applicationClassName;
        return this;
    }

    /**
     * Current application FQN
     *
     * @return FQN
     */
    public Class<?> applicationClassName() {
        return applicationClassName;
    }

    /**
     * Current VertX instance
     *
     * @return VertX
     */
    public Vertx vertx() {
        return vertx;
    }

    /**
     * Current jadde container
     *
     * @return the current container
     */
    public JaddeContainer container() {
        return container;
    }

    /**
     * All arguments (unmodifiable Map)
     *
     * @return Unmodifiable Map
     */
    public Map<String, String> arguments() {
        return Collections.unmodifiableMap(this.arguments);
    }

    public JaddeApplicationContext withClasspathResolver(final ClasspathResolver resolver) {
        this.classpathResolver = resolver;
        return this;
    }

    public ClasspathResolver classpathResolver() {
        return classpathResolver;
    }

    /**
     * The associated key => value argument
     *
     * @param key target key
     * @return optional argument
     */
    public Optional<String> argument(final String key) {
        return Optional.ofNullable(this.arguments.get(key));
    }

    /**
     * Bind strings arguments
     *
     * @param arguments arguments array
     * @return current instance (Fluent)
     */
    public JaddeApplicationContext withArguments(String[] arguments) {
        this.checkBuildIntegrityOrFail();
        Stream.of(arguments)
                .forEach(arg -> {
                    final Matcher argumentMatcher = ARGUMENT_PATTERN.matcher(arg);
                    if (argumentMatcher.matches() && argumentMatcher.groupCount() == 4) {
                        this.arguments.put(argumentMatcher.group(1), argumentMatcher.group(3));
                    }
                });
        return this;
    }

    public void finalise() {
        logger.info("Context initialization successfully ended");
        this.isBuild.set(true);
    }

    private void checkBuildIntegrityOrFail() {
        if (Boolean.TRUE.equals(this.isBuild.get())) {
            throw new ImmutableContextViolationException();
        }
    }

}
