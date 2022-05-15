package fr.jadde.fmk.bundle.web.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.HttpException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class MiddlewareProcessor {

    final List<InvokerMiddleware> invokerMiddlewares;

    final List<RouteMiddleware> routeMiddlewares;

    public MiddlewareProcessor() {
        this.invokerMiddlewares = Collections.synchronizedList(new ArrayList<>());
        this.routeMiddlewares = Collections.synchronizedList(new ArrayList<>());
    }

    public MiddlewareProcessor registerInvoker(final InvokerMiddleware middleware) {
        this.invokerMiddlewares.add(middleware);
        return this;
    }

    public MiddlewareProcessor registerRoute(final RouteMiddleware middleware) {
        this.routeMiddlewares.add(middleware);
        return this;
    }

    public Handler<RoutingContext> handler(final Object invoker, final Method delegate) {
        return routingContext -> {
            log.debug("Handle process middleware");
            this.next(routingContext);
            this.next(invoker, delegate);
            routingContext.next();
        };
    }

    public void next(final Object invoker, final Method delegate) {
        this.recursiveProcess(invoker, delegate, new LinkedList<>(this.invokerMiddlewares));
    }

    public void next(final RoutingContext context) {
        this.recursiveProcess(context, new LinkedList<>(this.routeMiddlewares));
    }

    private void recursiveProcess(final RoutingContext context, final Queue<RouteMiddleware> middlewares) {
        if (!middlewares.isEmpty()) {
            final RouteMiddleware middleware = middlewares.poll();
            try {
                middleware.next(context);
                this.recursiveProcess(context, middlewares);
            } catch (Exception e) {
                log.warn("Method middleware invocation error", e);
                throw new HttpException(500, e.getMessage());
            }
        }
    }

    private void recursiveProcess(final Object invoker, final Method delegate, final Queue<InvokerMiddleware> middlewares) {
        if (!middlewares.isEmpty()) {
            final InvokerMiddleware middleware = middlewares.poll();
            try {
                middleware.next(invoker, delegate);
                this.recursiveProcess(invoker, delegate, middlewares);
            } catch (Exception e) {
                log.warn("Method middleware invocation error", e);
                throw new HttpException(500, e.getMessage());
            }
        }
    }


}
