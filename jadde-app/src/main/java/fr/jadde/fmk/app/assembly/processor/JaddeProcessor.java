package fr.jadde.fmk.app.assembly.processor;

import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.jboss.weld.bean.ManagedBean;

import java.util.Collections;
import java.util.List;

/**
 * Allows you to process a bean
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeProcessor {

    public static final Logger logger = LoggerFactory.getLogger(JaddeProcessor.class);

    private final List<JaddeAnnotationProcessor> processors;

    /**
     * Ctor.
     *
     * @param processors available Jadde bean processors
     */
    private JaddeProcessor(final List<JaddeAnnotationProcessor> processors) {
        this.processors = Collections.unmodifiableList(processors);
    }

    /**
     * Starts bean processing from application context
     *
     * @param context target application context
     */
    @SuppressWarnings("rawtypes")
    public void process(final JaddeApplicationContext context) {
        final List<ManagedBean> beans = context.container().resolveAllBeans();
        beans.parallelStream().forEach(bean -> {
            context.container().resolveRealInstance(bean.getBeanClass()).ifPresentOrElse(o -> {
                logger.info("Start process '" + bean.getBeanClass() + "' processing");
                this.processInstance(o, processors);
            }, () -> logger.warn("Cannot process bean '" + bean.getBeanClass() + "' because missing in Jadde container"));
        });
    }

    private void processInstance(final Object bean, final List<JaddeAnnotationProcessor> processors) {
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
    public static JaddeProcessor create(final List<JaddeAnnotationProcessor> processors) {
        return new JaddeProcessor(processors);
    }

}
