package fr.jadde.fmk.bundle.dispatcher.api;

import fr.jadde.fmk.app.executor.bean.api.AbstractBeanWithContext;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Command;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Event;
import fr.jadde.fmk.bundle.dispatcher.api.payload.Headers;
import fr.jadde.fmk.container.annotation.JaddeBean;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

@JaddeBean
public final class JaddeHandlerToolbox extends AbstractBeanWithContext {

    public void dispatchTopicEvent(final String eventName, final Headers headers, final Object body) {
        final Event<Object> eventPayload = new Event<>(headers, body);
        this.eventBus().publish(eventName, Json.encode(eventPayload));
    }

    public void dispatchQueueEvent(final String eventName, final Headers headers, final Object body) {
        DeliveryOptions options = new DeliveryOptions();
        final Event<Object> eventPayload = new Event<>(headers, body);
        this.eventBus().send(eventName, Json.encode(eventPayload), options);
    }

    public <T> Future<T> dispatchCommand(final String commandName, final Headers headers, final Object body) {
        final Command<?> command = Command.build(headers, body);
        final Promise<T> responsePromise = Promise.promise();

        this.eventBus().<T>request(commandName, Json.encode(command), messageAsyncResult -> {
            if (messageAsyncResult.succeeded()) {
                responsePromise.complete(messageAsyncResult.result().body());
            } else {
                responsePromise.fail(messageAsyncResult.cause());
            }
        });
        return responsePromise.future();
    }

    private EventBus eventBus() {
        return this.context().vertx().eventBus();
    }

}
