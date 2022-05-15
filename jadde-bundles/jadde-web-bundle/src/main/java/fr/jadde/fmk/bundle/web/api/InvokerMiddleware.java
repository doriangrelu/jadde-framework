package fr.jadde.fmk.bundle.web.api;

import java.lang.reflect.Method;

@FunctionalInterface
public interface InvokerMiddleware {

    void next(final Object invoker, final Method delegate) throws Exception;

}
