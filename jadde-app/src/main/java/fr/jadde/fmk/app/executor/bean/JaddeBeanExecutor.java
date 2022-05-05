package fr.jadde.fmk.app.executor.bean;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.api.BeanWithContext;
import fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor;
import fr.jadde.fmk.app.executor.bundle.api.JaddeBundle;
import fr.jadde.fmk.app.executor.bean.tools.BeanUtils;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.*;

/**
 * Allows you to process a bean
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeBeanExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JaddeBeanExecutor.class);

    private final List<JaddeBeanProcessor> processors;

    /**
     * Ctor.
     *
     * @param processors available Jadde bean processors
     */
    private JaddeBeanExecutor(final List<JaddeBeanProcessor> processors) {
        final List<JaddeBeanProcessor> orderedProcessors = new ArrayList<>(processors);
        orderedProcessors.sort(Comparator.comparingInt(JaddeBeanProcessor::priorityOrder));
        this.processors = Collections.unmodifiableList(orderedProcessors);
    }

    /**
     * Starts bean processing from application context
     *
     * @param context target application context
     */
    public void execute(final JaddeApplicationContext context) {
        BeanUtils.getSafeBeans(context, JaddeBundle.class).parallelStream().forEach(bean -> {
            this.handleContext(bean, context);
            context.container().resolve(bean.getClass()).ifPresentOrElse(o -> {
                logger.info("Start process '" + bean.getClass() + "' processing");
                this.processInstance(o, processors);
            }, () -> logger.warn("Cannot process bean '" + bean.getClass() + "' because missing in Jadde container"));
        });
    }

    private void handleContext(final Object bean, final JaddeApplicationContext context) {
        if (bean instanceof BeanWithContext beanWithContext) {
            beanWithContext.setContext(context);
        }
    }

    private void processInstance(final Object bean, final List<JaddeBeanProcessor> processors) {
        processors.stream()
                .filter(processor -> processor.doesSupport(bean))
                .forEach(processor -> {
                    final Promise<Void> processPromise = Promise.promise();
                    processor.process(bean, processPromise);
                    processPromise.future().toCompletionStage().toCompletableFuture().join();
                });
    }

    /**
     * The processor instanciator !
     *
     * @param processors target available processors
     * @return the Jadde Processor ! 😍
     */
    public static JaddeBeanExecutor create(final List<JaddeBeanProcessor> processors) {
        return new JaddeBeanExecutor(processors);
    }

}
