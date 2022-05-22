package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.JaddeBeanExecutor;
import fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor;
import fr.jadde.fmk.app.executor.bundle.JaddeBundleExecutor;
import fr.jadde.fmk.app.executor.bundle.api.JaddeBundle;
import fr.jadde.fmk.container.annotation.JaddeBean;
import io.vertx.core.json.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Allows you to start the assembly of the VertX application
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
@Slf4j
public class JaddeApplicationAssembly {


    @SuppressWarnings("unchecked")
    public void processAssembly(final JaddeApplicationContext context) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (null == context) {
            throw new IllegalStateException("Cannot start assembly, missing context");
        }

        log.debug("Resolves beans processors");
        final List<JaddeBeanProcessor> processors = (List<JaddeBeanProcessor>) context.classpathResolver().resolveBySubtype(JaddeBeanProcessor.class)
                .parallelStream()
                .map(context.container()::registerAndGetInstance)
                .filter(Objects::nonNull)
                .toList();

        log.debug("Define context for beans processors");
        processors.forEach(jaddeBeanProcessor -> jaddeBeanProcessor.setContext(context));

        log.debug("Resolves bundles");
        context.classpathResolver().resolveBySubtype(JaddeBundle.class)
                .parallelStream()
                .forEach(context.container()::registerInstance);

        log.debug("Resolves beans");
        context.classpathResolver().resolveByAnnotation(JaddeBean.class)
                .parallelStream()
                .forEach(context.container()::registerInstance);

        log.debug("Handle dependency injection");
        JaddeContainerLifecycle.doInject(context);
        JaddeContainerLifecycle.hookStart(context);
        JaddeContainerLifecycle.hookStop(context);


        log.info("'{}' processors found, start application processing", processors.size());
        log.debug("Processor list -> {}", Json.encode(processors.stream().map(Object::toString).toList()));

        JaddeBundleExecutor.create().execute(context);
        context.finalise();

        JaddeBeanExecutor.create(processors).execute(context);

        stopWatch.stop();
        log.info("Application successfully assembled in {}ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

}
