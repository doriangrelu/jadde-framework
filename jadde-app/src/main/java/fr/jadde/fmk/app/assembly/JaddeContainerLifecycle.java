package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.annotation.Start;
import fr.jadde.fmk.app.executor.bean.annotation.Stop;
import fr.jadde.fmk.app.executor.bean.tools.BeanUtils;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
public class JaddeContainerLifecycle {

    public static void doInject(final JaddeApplicationContext context) {
        final JaddeDependencyInjector injectorProcessor = new JaddeDependencyInjector(context);
        BeanUtils.getSafeBeans(context).parallelStream()
                .filter(injectorProcessor::doesSupport)
                .forEach(injectorProcessor::process);
    }

    public static void hookStart(final JaddeApplicationContext context) {
        handleLifecycle(context, Start.class);
    }

    public static void hookStop(final JaddeApplicationContext context) {
        final Thread onStopHook = new Thread(() -> handleLifecycle(context, Stop.class), "THREAD::STOP_HOOK");
        Runtime.getRuntime().addShutdownHook(onStopHook);
    }

    private static void handleLifecycle(final JaddeApplicationContext context, final Class<? extends Annotation> targetAnnotationTrigger) {
        log.debug("For {} lifecycle, resolve and process asynchronous call(s)", targetAnnotationTrigger.getSimpleName());
        final List<CompletableFuture<Void>> futures = BeanUtils.getSafeBeans(context).parallelStream()
                .flatMap(bean -> Stream.of(bean.getClass().getMethods())
                        .filter(method -> AnnotationUtils.isAnnotationPresent(method, targetAnnotationTrigger))
                        .map(method -> CompletableFuture.runAsync(() -> {
                                    try {
                                        method.invoke(bean);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        throw new IllegalStateException("Cannot call start method for '" + bean.getClass().getName() + "' --> '" + method.getName() + "'", e);
                                    }
                                })
                        )
                ).toList();
        log.debug("For {} lifecycle, await completion for {} stage(s)", targetAnnotationTrigger.getSimpleName(), futures.size());
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        log.debug("For {} lifecycle, await successfully ended for {} stage(s)", targetAnnotationTrigger.getSimpleName(), futures.size());
    }

}
