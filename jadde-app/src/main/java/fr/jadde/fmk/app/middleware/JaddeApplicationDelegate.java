package fr.jadde.fmk.app.middleware;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.middleware.api.JaddeApplicationMiddleware;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaddeApplicationDelegate {

    private static final Pattern CLASS_PATTERN = Pattern.compile("^([a-z0-9]+[a-zA-Z\\.0-9]+).*$");

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationDelegate.class);

    private JaddeApplicationDelegate() {

    }

    public void startDequeue(final JaddeApplicationContext context) {
        logger.info("Middleware build start");
        final List<JaddeApplicationMiddleware> beans = new ArrayList<>(context.container().resolveAll(JaddeApplicationMiddleware.class));
        if (!beans.isEmpty()) {
            final JaddeApplicationMiddleware startMiddleware = beans.remove(0);
            this.handleSetMiddleware(startMiddleware, new LinkedList<>(beans));
            logger.info("Middleware start dequeue '" + this.formatClassName(startMiddleware) + "'");
            final boolean status = startMiddleware.next(context);
            logger.info("Middleware dequeue finished with status '" + status + "'");
        } else {
            logger.warn("Have not middleware");
        }

    }

    public void handleSetMiddleware(final JaddeApplicationMiddleware currentMiddleware, final Queue<JaddeApplicationMiddleware> nexts) {
        if (!nexts.isEmpty()) {
            final JaddeApplicationMiddleware nextMiddleware = nexts.poll();
            logger.info("Add middleware '" + this.formatClassName(currentMiddleware) + "' ==> '" + this.formatClassName(nextMiddleware) + "'");
            currentMiddleware.setNext(nextMiddleware);
            this.handleSetMiddleware(nextMiddleware, nexts);
        } else {
            logger.info("End middleware build '" + this.formatClassName(currentMiddleware) + "'");
        }
    }

    private String formatClassName(final Object o) {
        final Matcher matcher = CLASS_PATTERN.matcher(o.getClass().getName());
        return matcher.matches() && matcher.groupCount() == 1 ? matcher.group(1) : o.getClass().getSimpleName();
    }

    public static JaddeApplicationDelegate create() {
        return new JaddeApplicationDelegate();
    }

}
