package fr.jadde.fmk.app.context;

import io.vertx.core.Vertx;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JaddeApplicationContext {

    public static final Pattern ARGUMENT_PATTERN = Pattern
            .compile("^([a-z]+([a-z\\-\\.][a-z]+)*)=([a-z0-9]+([a-z\\-\\.][a-z0-9]+)*)$");

    private final Class<?> applicationClassName;

    private final Vertx vertx;

    private final Map<String, String> arguments;

    private JaddeApplicationContext(Class<?> applicationClassName, final Vertx vertx) {
        this.applicationClassName = applicationClassName;
        this.vertx = vertx;
        this.arguments = new ConcurrentHashMap<>();
    }

    public Class<?> applicationClassName() {
        return applicationClassName;
    }

    public Vertx vertx() {
        return vertx;
    }

    public Map<String, String> arguments() {
        return Collections.unmodifiableMap(this.arguments);
    }

    public Optional<String> argument(final String key) {
        return Optional.ofNullable(this.arguments.get(key));
    }

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


    public static JaddeApplicationContext create(final Class<?> applicationClassName, final Vertx vertx) {
        return new JaddeApplicationContext(applicationClassName, vertx);
    }

}
