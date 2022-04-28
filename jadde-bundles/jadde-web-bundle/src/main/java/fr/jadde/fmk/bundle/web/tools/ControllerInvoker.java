package fr.jadde.fmk.bundle.web.tools;

import fr.jadde.fmk.bundle.web.annotation.BodyParam;
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
import java.util.stream.Stream;

public class ControllerInvoker {

    private static final Logger logger = LoggerFactory.getLogger(ControllerInvoker.class);

    public static Future<Object> doInvoke(final RoutingContext context, final Object delegate, final Method method) {
        logger.info("Request {}[{}] --> {}[{}]", context.request().method(), context.normalizedPath(), delegate.getClass().getName(), method.getName());
        final List<Object> parameters = Stream.of(method.getParameters())
                .map(parameter -> mapRoutingContext(context, parameter))
                .map(parameter -> mapPathParameters(context, parameter))
                .map(parameter -> mapHeaderParameters(context, parameter))
                .map(parameter -> mapQueryParameters(context, parameter))
                .map(parameter -> mapBody(context, parameter))
                .map(parameter -> mapDefault(context, parameter))
                .filter(Objects::nonNull)
                .toList();

        if (method.getParameterCount() != parameters.size()) {
            throw new HttpException(400, "Bad request, request missing parameter");
        }

        try {
            final Object invocationResult = method.invoke(delegate, parameters.toArray(Object[]::new));
            return Future.succeededFuture(invocationResult);
        } catch (Throwable e) {
            return Future.failedFuture(e);
        }
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
