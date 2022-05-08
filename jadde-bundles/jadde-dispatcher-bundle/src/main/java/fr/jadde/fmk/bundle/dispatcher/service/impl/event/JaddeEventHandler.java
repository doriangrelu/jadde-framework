package fr.jadde.fmk.bundle.dispatcher.service.impl.event;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Event;
import fr.jadde.fmk.bundle.dispatcher.api.utils.HeadersBuilder;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JaddeEventHandler {

    public static final Logger logger = LoggerFactory.getLogger(JaddeEventHandler.class);

    public static void handle(final JaddeApplicationContext context, final Object invoker, final Method delegate, final String event) {
        logger.info("Register event consumer '{}': {} --> {}", event, invoker.getClass().getName(), delegate.getName());
        context.vertx().eventBus().<String>consumer(event, message -> {
            logger.debug("Trying dispatch '{}' on {} --> {}", event, invoker.getClass().getName(), delegate.getName());
            invoke(invoker, delegate, message);
        });
    }

    private static void invoke(final Object invoker, final Method delegate, final Message<String> message) {
        try {
            delegate.invoke(invoker, Json.decodeValue(message.body(), Event.class));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot dispatch event on '" + invoker.getClass().getName() + "' --> '" + delegate.getName() + "'");
        }
    }


}
