package fr.jadde.fmk.bundle.web.tools;

import fr.jadde.fmk.bundle.web.annotation.BodyParam;
import fr.jadde.fmk.bundle.web.api.MiddlewareProcessor;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class ControllerInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ControllerInvoker.class);

    @SuppressWarnings("unchecked")
    public static Future<Object> doInvoke(final RoutingContext context, final Object invoker, final Method delegate, final MiddlewareProcessor middlewareProcessor) {
        logger.info("[http-{}] DISPATCH --> {}[{}]", context.request().headers().get("uuid"), invoker.getClass().getName(), delegate.getName());
        final List<Object> parameters = Stream.of(delegate.getParameters())
                .map(parameter -> mapRoutingContext(context, parameter))
                .map(parameter -> mapPathParameters(context, parameter))
                .map(parameter -> mapHeaderParameters(context, parameter))
                .map(parameter -> mapQueryParameters(context, parameter))
                .map(parameter -> mapBody(context, parameter))
                .map(parameter -> mapDefault(context, parameter))
                .filter(Objects::nonNull)
                .toList();
        if (delegate.getParameterCount() != parameters.size()) {
            throw new HttpException(400, "Bad request, request missing parameter");
        }
        try {
            final Object[] arguments = parameters.toArray(Object[]::new);
            handleMiddleware(invoker, delegate, arguments, middlewareProcessor);
            final Object invocationResult = delegate.invoke(invoker, arguments);
            if (invocationResult instanceof Future<?> futureResult) {
                return (Future<Object>) futureResult;
            }
            if (invocationResult instanceof CompletableFuture<?> completableFuture) {
                return (Future<Object>) Future.fromCompletionStage(completableFuture);
            }
            return Future.succeededFuture(invocationResult);
        } catch (Throwable e) {
            return Future.failedFuture(e);
        }
    }

    private static void handleMiddleware(final Object invoker, final Method delegate, final Object[] arguments, final MiddlewareProcessor middlewareProcessor) {
        middlewareProcessor.next(invoker, delegate, arguments);
    }

    private static Object mapPathParameters(final RoutingContext context, final Object target) {
        if (target instanceof Parameter parameter && parameter.isAnnotationPresent(PathParam.class)) {
            final String parameterName = parameter.getAnnotation(PathParam.class).value();
            return context.pathParam(parameterName);
        }
        return target;
    }

    private static Object mapQueryParameters(final RoutingContext context, final Object target) {
        if (target instanceof Parameter parameter && parameter.isAnnotationPresent(QueryParam.class)) {
            final String parameterName = parameter.getAnnotation(QueryParam.class).value();
            return context.queryParam(parameterName);
        }
        return target;
    }

    private static Object mapHeaderParameters(final RoutingContext context, final Object target) {
        if (target instanceof Parameter parameter && parameter.isAnnotationPresent(HeaderParam.class)) {
            final String parameterName = parameter.getAnnotation(HeaderParam.class).value();
            return context.request().getHeader(parameterName);
        }
        return target;
    }

    private static Object mapBody(final RoutingContext context, final Object target) {
        if (target instanceof Parameter parameter && parameter.isAnnotationPresent(BodyParam.class)) {
            if (parameter.getType().isAssignableFrom(JsonObject.class)) {
                return context.getBodyAsJson();
            }
            return context.getBodyAsJson().mapTo(parameter.getType());
        }
        return target;
    }

    private static Object mapDefault(final RoutingContext context, final Object target) {
        if (target instanceof Parameter parameter) {
            return context.getBodyAsJson().mapTo(parameter.getType());
        }
        return target;
    }

    private static Object mapRoutingContext(final RoutingContext context, final Object target) {
        if (target instanceof Parameter parameter && parameter.getType().isAssignableFrom(RoutingContext.class)) {
            return context;
        }
        return target;
    }


}
