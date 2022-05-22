package fr.jadde.fmk.bundle.validation;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;
import fr.jadde.fmk.bundle.validation.middleware.RouteMethodDispatchMiddleware;
import fr.jadde.fmk.bundle.web.api.MiddlewareProcessor;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JaddeValidationBundle extends AbstractJaddeBundle {

    public static final String NAME = "jadde.validation.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        log.info("Successfully loaded validation bundle");
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        context.container().bindInstance(factory);
        context.container().bindInstance(validator);

        final RouteMethodDispatchMiddleware middleware = new RouteMethodDispatchMiddleware(context.container());
        context.container().resolve(MiddlewareProcessor.class).orElseThrow().registerInvoker(middleware);

        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public short priorityOrder() {
        return -1;
    }
}
