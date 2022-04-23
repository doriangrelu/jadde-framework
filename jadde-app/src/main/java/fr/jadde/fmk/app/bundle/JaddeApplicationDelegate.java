package fr.jadde.fmk.app.bundle;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.bundle.api.JaddeApplicationBundle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaddeApplicationDelegate {

    private static final Pattern CLASS_PATTERN = Pattern.compile("^([a-z0-9]+[a-zA-Z\\.0-9]+).*$");

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationDelegate.class);

    private JaddeApplicationDelegate() {

    }

    public void startDequeue(final JaddeApplicationContext context) {
        logger.info("Middleware build start");
        final List<JaddeApplicationBundle> beans = this.sortBundles(context.container().resolveAll(JaddeApplicationBundle.class));
        final Queue<JaddeApplicationBundle> bundleQueue = new LinkedList<>(beans);
        if (!bundleQueue.isEmpty()) {
            final JaddeApplicationBundle startMiddleware = bundleQueue.poll();
            this.handleSetBundleGraph(startMiddleware, bundleQueue);
            logger.info("Middleware start dequeue '" + this.formatClassName(startMiddleware) + "'");
            final boolean status = startMiddleware.next(context);
            logger.info("Middleware dequeue finished with status '" + status + "'");
        } else {
            logger.warn("Have not middleware");
        }

    }

    public void handleSetBundleGraph(final JaddeApplicationBundle currentMiddleware, final Queue<JaddeApplicationBundle> nexts) {
        if (!nexts.isEmpty()) {
            final JaddeApplicationBundle nextMiddleware = nexts.poll();
            logger.info("Add middleware '" + this.formatClassName(currentMiddleware) + "' ==> '" + this.formatClassName(nextMiddleware) + "'");
            currentMiddleware.setNext(nextMiddleware);
            this.handleSetBundleGraph(nextMiddleware, nexts);
        } else {
            logger.info("End middleware build '" + this.formatClassName(currentMiddleware) + "'");
        }
    }

    private String formatClassName(final Object o) {
        final Matcher matcher = CLASS_PATTERN.matcher(o.getClass().getName());
        return matcher.matches() && matcher.groupCount() == 1 ? matcher.group(1) : o.getClass().getSimpleName();
    }

    private List<JaddeApplicationBundle> sortBundles(final List<JaddeApplicationBundle> bundles) {
        return bundles.stream()
                .sorted(Comparator.comparingInt(JaddeApplicationBundle::priorityOrder))
                .toList();
    }

    public static JaddeApplicationDelegate create() {
        return new JaddeApplicationDelegate();
    }

}
