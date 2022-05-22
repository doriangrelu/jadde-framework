package fr.jadde.fmk.app.executor.bundle;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.tools.BeanUtils;
import fr.jadde.fmk.app.executor.bundle.api.JaddeBundle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JaddeBundleExecutor {

    private static final Pattern CLASS_PATTERN = Pattern.compile("^([a-z0-9]+[a-zA-Z\\.0-9]+).*$");

    private static final Logger logger = LoggerFactory.getLogger(JaddeBundleExecutor.class);

    private JaddeBundleExecutor() {
    }

    public void execute(final JaddeApplicationContext context) {
        logger.debug("Bundle build start");
        final List<JaddeBundle> beans = this.sortBundles(context.container().resolveAll(JaddeBundle.class));
        final Queue<JaddeBundle> bundleQueue = new LinkedList<>(beans);
        if (!bundleQueue.isEmpty()) {
            final JaddeBundle startBundle = bundleQueue.poll();
            this.handleSetBundleGraph(startBundle, bundleQueue);
            logger.debug("Bundle start dequeue '" + this.formatClassName(startBundle) + "'");
            final boolean status = startBundle.next(context);
            logger.debug("Bundle dequeue finished with status '" + status + "'");
        } else {
            logger.warn("Have not bundle");
        }
    }

    public void handleSetBundleGraph(final JaddeBundle currentBundle, final Queue<JaddeBundle> nexts) {
        if (!nexts.isEmpty()) {
            final JaddeBundle nextBundle = nexts.poll();
            logger.info("Add bundle '" + this.formatClassName(currentBundle) + "' ==> '" + this.formatClassName(nextBundle) + "'");
            currentBundle.setNext(nextBundle);
            this.handleSetBundleGraph(nextBundle, nexts);
        } else {
            logger.info("End bundle build '" + this.formatClassName(currentBundle) + "'");
        }
    }

    private String formatClassName(final Object o) {
        final Matcher matcher = CLASS_PATTERN.matcher(o.getClass().getName());
        return matcher.matches() && matcher.groupCount() == 1 ? matcher.group(1) : o.getClass().getSimpleName();
    }

    private List<JaddeBundle> sortBundles(final List<JaddeBundle> bundles) {
        return bundles.stream()
                .sorted(Comparator.comparingInt(JaddeBundle::priorityOrder))
                .toList();
    }

    public static JaddeBundleExecutor create() {
        return new JaddeBundleExecutor();
    }

}
