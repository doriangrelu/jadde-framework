package fr.jadde.fmk.app.assembly.processor;

import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.middleware.api.JaddeApplicationMiddleware;
import fr.jadde.fmk.app.tools.BeanUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Allows you to process a bean
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JaddeProcessor.class);

    private final Set<JaddeAnnotationProcessor> processors;

    /**
     * Ctor.
     *
     * @param processors available Jadde bean processors
     */
    private JaddeProcessor(final Set<JaddeAnnotationProcessor> processors) {
        this.processors = Collections.unmodifiableSet(processors);
    }

    /**
     * Starts bean processing from application context
     *
     * @param context target application context
     */
    @SuppressWarnings("rawtypes")
    public void process(final JaddeApplicationContext context) {
        BeanUtils.getSafeBeans(context, JaddeApplicationMiddleware.class).forEach(bean -> {
            context.container().resolve(bean.getClass()).ifPresentOrElse(o -> {
                logger.info("Start process '" + bean.getClass() + "' processing");
                this.processInstance(o, processors);
            }, () -> logger.warn("Cannot process bean '" + bean.getClass() + "' because missing in Jadde container"));
        });
    }

    private void processInstance(final Object bean, final Set<JaddeAnnotationProcessor> processors) {
        processors.parallelStream()
                .filter(processor -> processor.doesSupport(bean))
                .forEach(processor -> processor.process(bean));
    }

    /**
     * The processor instanciator !
     *
     * @param processors target available processors
     * @return the Jadde Processor ! 😍
     */
    public static JaddeProcessor create(final Set<JaddeAnnotationProcessor> processors) {
        return new JaddeProcessor(processors);
    }

}
