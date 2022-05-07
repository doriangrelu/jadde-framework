package fr.jadde.fmk.bundle.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ResponseLoggerHandler implements Handler<RoutingContext> {

    public static final Logger logger = LoggerFactory.getLogger(RequestLoggerHandler.class);

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.request().headers().set("uuid", UUID.randomUUID().toString());
        logger.info(
                "[http-{}] RESPOND --> {} {}",
                routingContext.request().headers().get("uuid"),
                routingContext.response().getStatusCode(),
                routingContext.response().getStatusMessage()
        );
        routingContext.next();
    }

}