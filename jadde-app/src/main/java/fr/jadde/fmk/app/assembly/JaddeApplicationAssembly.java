package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.executor.bean.JaddeBeanExecutor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.JaddeBundleExecutor;
import fr.jadde.fmk.app.executor.bundle.api.JaddeBundle;
import fr.jadde.fmk.container.annotation.JaddeBean;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Allows you to start the assembly of the VertX application
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeApplicationAssembly {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationAssembly.class);

    public void processAssembly(final JaddeApplicationContext context) {
        if (null == context) {
            throw new IllegalStateException("Cannot start assembly, missing context");
        }
        
        logger.info("Resolves beans processors");
        final Set<fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor> processors = context.classpathResolver().resolveBySubtype(fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor.class)
                .parallelStream()
                .map(context.container()::getInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        logger.info("Define context for beans processors");
        processors.forEach(jaddeBeanProcessor -> jaddeBeanProcessor.setContext(context));

        logger.info("Resolves bundles");
        context.classpathResolver().resolveBySubtype(JaddeBundle.class)
                .parallelStream()
                .forEach(context.container()::registerInstance);

        logger.info("Resolves beans");
        context.classpathResolver().resolveByAnnotation(JaddeBean.class)
                .parallelStream()
                .forEach(context.container()::registerInstance);

        logger.info("'" + processors.size() + "' founded, start application processing");
        logger.debug("Processor list -> " + Json.encode(processors.stream().map(Object::toString).toList()));

        JaddeBundleExecutor.create().execute(context);
        context.finalise();

        JaddeBeanExecutor.create(processors).execute(context);

    }

}
