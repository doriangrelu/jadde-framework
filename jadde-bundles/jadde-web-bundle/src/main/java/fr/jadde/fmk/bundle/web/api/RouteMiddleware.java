package fr.jadde.fmk.bundle.web.api;

import io.vertx.ext.web.RoutingContext;

@FunctionalInterface
public interface RouteMiddleware {

    void next(final RoutingContext context) throws Exception;

}
