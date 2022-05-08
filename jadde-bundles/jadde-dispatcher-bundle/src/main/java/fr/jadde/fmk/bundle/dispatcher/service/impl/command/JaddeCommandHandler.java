package fr.jadde.fmk.bundle.dispatcher.service.impl.command;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Command;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JaddeCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(JaddeCommandHandler.class);

    public static void handle(final JaddeApplicationContext context, final Object invoker, final Method delegate, final String event) {
        logger.info("Register command consumer '{}': {} --> {}", event, invoker.getClass().getName(), delegate.getName());
        final MessageConsumer<String> consumer = context.vertx().eventBus().consumer(event);
        consumer.handler(handler -> {
            invoke(invoker, delegate, handler.body())
                    .onSuccess(handler::reply)
                    .onFailure(throwable -> {
                        logger.warn("Command execution failure '{}': {} --> {}", event, invoker.getClass().getName(), delegate.getName(), throwable);
                        handler.fail(500, throwable.getMessage());
                    });
        });
    }

    private static Future<Object> invoke(final Object invoker, final Method delegate, final String message) {
        try {
            final Command<?> command = Json.decodeValue(message, Command.class);
            final Promise<Object> responsePromise = Promise.promise();
            delegate.invoke(invoker, command.dispatch(responsePromise));
            return responsePromise.future();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot dispatch event on '" + invoker.getClass().getName() + "' --> '" + delegate.getName() + "'");
        }
    }

}
