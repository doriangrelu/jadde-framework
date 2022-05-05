package fr.jadde.fmk.bundle.web;

import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.app.context.configuration.Parameter;
import fr.jadde.fmk.bundle.web.annotation.RestController;
import fr.jadde.fmk.bundle.web.exceptions.BadEndpointDeclarationException;
import fr.jadde.fmk.bundle.web.exceptions.WebInitializationException;
import fr.jadde.fmk.bundle.web.tools.Endpoint;
import fr.jadde.fmk.bundle.web.tools.ControllerInvoker;
import fr.jadde.fmk.bundle.web.tools.PathUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Processes the web beans
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public final class JaddeWebProcessor extends AbstractJaddeBeanProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JaddeWebProcessor.class);

    public static final char DEFAULT_PATH = '/';

    private final Set<Class<? extends Annotation>> supportedMethods;


    public JaddeWebProcessor() {
        this.supportedMethods = Set.of(POST.class, GET.class, PUT.class, PATCH.class, DELETE.class);
    }

    @Override
    public void process(Object target) {
        final String rootPath = this.extractRootPath(target.getClass());
        final Map<Endpoint, Method> endpoints = new HashMap<>();
        Stream.of(target.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Path.class))
                .forEach(method -> {
                    final Pair<Endpoint, Method> declaration = this.processMethod(rootPath, method);
                    endpoints.put(declaration.getKey(), declaration.getValue());
                });
        try {
            final Router router = (Router) this.context().waitForParameter(Parameter.ROUTER.parameterName()).toCompletionStage().toCompletableFuture().get();
            endpoints.forEach((endpoint, method) -> {
                this.processEndpointRegistration(router, target, method, endpoint);
            });
        } catch (InterruptedException | ExecutionException e) {
            throw new WebInitializationException("Cannot end web processor because missing router instance in context", e);
        }
    }

    private void processEndpointRegistration(final Router router, final Object delegate, final Method method, final Endpoint endpoint) {
        logger.info("Deploy {}[{}] --> {}[{}]", endpoint.method().toUpperCase(), endpoint.path(), delegate.getClass(), method.getName());
        this.makeRoute(router, endpoint.path(), HttpMethod.valueOf(endpoint.method()))
                .handler(BodyHandler.create())
                .produces(endpoint.produces())
                .respond(routingContext -> ControllerInvoker.doInvoke(routingContext, delegate, method));
    }

    private Route makeRoute(final Router router, final String path, final HttpMethod method) {
        return router.getRoutes()
                .stream()
                .filter(route -> route.getPath().equals(path))
                .findFirst()
                .orElse(router.route().path(path))
                .method(method);
    }

    private Pair<Endpoint, Method> processMethod(final String rootPath, final Method method) {
        final String path = PathUtils.sanitizePath(method.getAnnotation(Path.class).value());
        final String completePath = PathUtils.composePath(rootPath, path);
        final String httpMethod = this.extractMethod(method);
        final String consumes = this.extractConsumes(method);
        final String produces = this.extractProduces(method);

        return Pair.of(new Endpoint(completePath, httpMethod, consumes, produces), method);
    }

    private String extractConsumes(final Method method) {
        if (method.isAnnotationPresent(Consumes.class)) {
            String[] consumes = method.getAnnotation(Consumes.class).value();
            if (consumes.length != 1) {
                throw new BadEndpointDeclarationException(method.getDeclaringClass(), method, "Must have on consumes type");
            }
            return consumes[0];
        }
        return MediaType.APPLICATION_JSON;
    }

    private String extractProduces(final Method method) {
        if (method.isAnnotationPresent(Produces.class)) {
            String[] produces = method.getAnnotation(Produces.class).value();
            if (produces.length != 1) {
                throw new BadEndpointDeclarationException(method.getDeclaringClass(), method, "Must have on produces type");
            }
            return produces[0];
        }
        return MediaType.APPLICATION_JSON;
    }

    private String extractMethod(final Method method) {
        return this.supportedMethods.stream()
                .filter(method::isAnnotationPresent)
                .map(verb -> verb.getSimpleName().toUpperCase(Locale.ROOT))
                .findFirst()
                .orElse(GET.class.getSimpleName().toUpperCase(Locale.ROOT));
    }

    private String extractRootPath(final Class<?> target) {
        if (target.isAnnotationPresent(Path.class)) {
            return StringUtils.strip(target.getAnnotation(Path.class).value(), String.valueOf(DEFAULT_PATH)).trim();
        }
        return "";
    }


    @Override
    public boolean doesSupport(Object target) {
        return null != target && target.getClass().isAnnotationPresent(RestController.class);
    }

    @Override
    public int priorityOrder() {
        return 2;
    }

}
