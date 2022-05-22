package fr.jadde.fmk.bundle.validation.middleware;

import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.bundle.validation.annotation.Validated;
import fr.jadde.fmk.bundle.validation.domain.WebFieldViolationError;
import fr.jadde.fmk.bundle.validation.domain.WebValidationError;
import fr.jadde.fmk.bundle.web.api.InvokerMiddleware;
import fr.jadde.fmk.container.api.JaddeContainer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.HttpException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class RouteMethodDispatchMiddleware implements InvokerMiddleware {

    private final JaddeContainer container;

    public RouteMethodDispatchMiddleware(final JaddeContainer container) {
        this.container = container;
    }

    @Override
    public void next(final Object invoker, final Method delegate, final Object[] arguments) throws Exception {
        if (this.doesSupport(invoker)) {
            this.process(invoker, delegate, arguments);
        }
    }


    public void process(final Object invoker, final Method delegate, final Object[] arguments) {
        final Parameter[] parameters = delegate.getParameters();

        if (arguments.length != parameters.length) {
            throw new IllegalArgumentException("Cannot process argument validation because have not same number of parameters / arguments for " +
                    invoker.getClass().getName() + " --> " + delegate.getName());
        }

        final List<Object> needValidationArguments = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            if (AnnotationUtils.isAnnotationPresent(parameter, Valid.class)) {
                needValidationArguments.add(arguments[i]);
            }
        }

        final Validator validator = this.container.resolve(Validator.class).orElseThrow();

        final List<WebFieldViolationError> errors = new ArrayList<>();
        needValidationArguments.stream()
                .flatMap(arg -> validator.validate(arg).stream())
                .forEach(violation -> {
                    errors.add(new WebFieldViolationError(violation.getPropertyPath().toString(), violation.getMessage()));
                });


        if (!errors.isEmpty()) {
            throw new HttpException(400, Json.encode(new WebValidationError(400, errors)));
        }
    }

    private void processArgument(final Object target, final Method method) {

    }


    public boolean doesSupport(final Object target) {
        return null != target && AnnotationUtils.isAnnotationPresent(target.getClass(), Validated.class);
    }

}
