package fr.jadde.fmk.app.executor.bean;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.JaddeBundle;
import fr.jadde.fmk.app.executor.bean.tools.BeanUtils;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.util.Collections;
import java.util.Set;

/**
 * Allows you to process a bean
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeBeanExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JaddeBeanExecutor.class);

    private final Set<fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor> processors;

    /**
     * Ctor.
     *
     * @param processors available Jadde bean processors
     */
    private JaddeBeanExecutor(final Set<fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor> processors) {
        this.processors = Collections.unmodifiableSet(processors);
    }

    /**
     * Starts bean processing from application context
     *
     * @param context target application context
     */
    public void execute(final JaddeApplicationContext context) {
        BeanUtils.getSafeBeans(context, JaddeBundle.class).forEach(bean -> {
            context.container().resolve(bean.getClass()).ifPresentOrElse(o -> {
                logger.info("Start process '" + bean.getClass() + "' processing");
                this.processInstance(o, processors);
            }, () -> logger.warn("Cannot process bean '" + bean.getClass() + "' because missing in Jadde container"));
        });
    }

    private void processInstance(final Object bean, final Set<fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor> processors) {
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
    public static JaddeBeanExecutor create(final Set<fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor> processors) {
        return new JaddeBeanExecutor(processors);
    }

}
