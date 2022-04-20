package fr.jadde.fmk.app.context;

import fr.jadde.fmk.container.JaddeContainer;
import fr.jadde.fmk.container.module.AbstractJaddeModule;
import io.vertx.core.Vertx;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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

    public static final Pattern ARGUMENT_PATTERN = Pattern
            .compile("^([a-z]+([a-z\\-\\.][a-z]+)*)=([a-z0-9]+([a-z\\-\\.][a-z0-9]+)*)$");

    private final Class<?> applicationClassName;

    private final Vertx vertx;

    private final JaddeContainer container;

    private final Map<String, String> arguments;

    /**
     * Constructor
     *
     * @param applicationClassName current application FQN
     * @param vertx                current VertX instance
     */
    private JaddeApplicationContext(Class<? extends AbstractJaddeModule> applicationClassName, final Vertx vertx) {
        this.applicationClassName = applicationClassName;
        this.vertx = vertx;
        this.arguments = new ConcurrentHashMap<>();
        this.container = JaddeContainer.create();
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
        Stream.of(arguments)
                .forEach(arg -> {
                    final Matcher argumentMatcher = ARGUMENT_PATTERN.matcher(arg);
                    if (argumentMatcher.matches() && argumentMatcher.groupCount() == 4) {
                        this.arguments.put(argumentMatcher.group(1), argumentMatcher.group(3));
                    }
                });
        return this;
    }

    /**
     * Create the context
     *
     * @param applicationClassName from application FQN
     * @param vertx                associated VertX instance
     * @return new context instance
     */
    public static JaddeApplicationContext create(final Class<? extends AbstractJaddeModule> applicationClassName, final Vertx vertx) {
        return new JaddeApplicationContext(applicationClassName, vertx);
    }

}
