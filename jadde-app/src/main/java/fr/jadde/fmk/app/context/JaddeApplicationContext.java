package fr.jadde.fmk.app.context;

import fr.jadde.fmk.app.JaddeApplication;
import fr.jadde.fmk.app.assembly.resolver.ClasspathResolver;
import fr.jadde.fmk.app.context.configuration.Configuration;
import fr.jadde.fmk.app.exception.ImmutableContextViolationException;
import fr.jadde.fmk.app.verticle.AbstractJaddeVerticle;
import fr.jadde.fmk.container.JaddeContainer;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public static final Pattern ARGUMENT_PATTERN = Pattern
            .compile("^([a-z]+([a-z\\-\\.][a-z]+)*)=([a-z0-9]+([a-z\\-\\.][a-z0-9]+)*)$");
    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationContext.class);
    private ClasspathResolver classpathResolver;

    private Class<?> applicationClassName;

    private Vertx vertx;

    private JaddeContainer container;

    private final Map<String, String> arguments;

    private final AtomicBoolean isBuild = new AtomicBoolean(false);

    private ConfigRetriever configRetriever;

    private Configuration configuration;

    private final Map<String, Object> parameters;

    public JaddeApplicationContext() {
        this.arguments = new ConcurrentHashMap<>();
        this.parameters = new ConcurrentHashMap<>();
    }


    public JaddeApplicationContext withContainer(final JaddeContainer container) {
        this.checkBuildIntegrityOrFail();
        this.container = container;
        return this;
    }

    public JaddeApplicationContext withVertX(final Vertx vertx) {
        this.checkBuildIntegrityOrFail();
        this.vertx = vertx;
        this.configRetriever = ConfigRetriever.create(vertx);
        this.configuration = new Configuration(this.configRetriever);
        return this;
    }

    public Configuration configuration() {
        return configuration;
    }

    public JaddeApplicationContext withParameter(final String name, final Object value) {
        this.parameters.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Future<T> waitForParameter(final String name) {
        if (this.parameters.containsKey(name)) {
            return (Future<T>) Future.succeededFuture(this.parameters.get(name));
        }
        final Promise<T> response = Promise.promise();
        final long task = this.vertx.setPeriodic(1000, aLong -> {
            if (this.parameters.containsKey(name)) {
                response.tryComplete((T) this.parameters.get(name));
            }
        });
        final long cancelTimer = this.vertx.setTimer(10000, aLong -> {
            response.tryFail("Timeout exception");
        });
        return response.future().onComplete(result -> {
            this.vertx.cancelTimer(task);
            this.vertx.cancelTimer(cancelTimer);
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T parameter(final String name) {
        return (T) this.parameters.get(name);
    }

    public JaddeApplicationContext unDeploy(final Class<? extends AbstractJaddeVerticle> verticleClass) {
        this.vertx.undeploy(verticleClass.getName(), voidAsyncResult -> {
            logger.info("Undeploy '" + verticleClass + "' does success > " + voidAsyncResult.succeeded());
        });
        return this;
    }

    public JaddeApplicationContext deploy(final Class<? extends AbstractJaddeVerticle> verticleClass) {
        try {
            final Constructor<?> constructor = Stream.of(verticleClass.getConstructors())
                    .filter(ctor -> ctor.getParameterCount() == 0)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Cannot deploy verticle '" + verticleClass + "' missing default constructor"));
            final AbstractJaddeVerticle verticle = (AbstractJaddeVerticle) constructor.newInstance();
            return this.deploy(verticle);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot deploy verticle '" + verticleClass + "'", e);
        }
    }

    public JaddeApplicationContext deploy(final AbstractJaddeVerticle verticle) {
        this.vertx.deployVerticle(verticle.withContext(this));
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

    public ConfigRetriever configRetriever() {
        return configRetriever;
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
