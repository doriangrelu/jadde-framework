package fr.jadde.fmk.app.assembly.processor;

import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class JaddeGenericProcessorResolver {

    private final JaddeApplicationContext context;

    private static final Logger logger = LoggerFactory.getLogger(JaddeGenericProcessorResolver.class);

    private final Set<JaddeAnnotationProcessor> processors;

    public JaddeGenericProcessorResolver(final JaddeApplicationContext context, final Set<JaddeAnnotationProcessor> processors) {
        this.context = context;
        this.processors = Collections.synchronizedSet(processors);
    }

    public void handleResolve(final Set<Class<?>> targetClasses) {
        logger.info("Handle process '" + targetClasses.size() + "' classes");
        targetClasses.parallelStream().forEach(aClass -> {
            logger.debug("Process '" + aClass + "'");
            if (aClass.isAnnotation() || aClass.isEnum() || aClass.isArray() || aClass.isInterface() || aClass.isPrimitive()) {
                return;
            }
         /*   final Object beanInstance = this.context.container().withBean(aClass);
            Arrays.asList(targetClasses.getClass().getAnnotations()).parallelStream().forEach(annotation -> {
                logger.trace("Process '" + aClass + "' ----> '" + annotation + "'");
                this.doResolveProcessorAndProcess(annotation, beanInstance);
            });
            */

        });

    }

    private <T extends Annotation> void doResolveProcessorAndProcess(final T annotation, final Object target) {
        this.processors.parallelStream()
                .filter(jaddeAnnotationProcessor -> jaddeAnnotationProcessor.doesSupport(annotation, target))
                .forEach(jaddeAnnotationProcessor -> jaddeAnnotationProcessor.process(annotation, target));
    }

}
