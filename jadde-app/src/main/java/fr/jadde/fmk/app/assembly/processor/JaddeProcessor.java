package fr.jadde.fmk.app.assembly.processor;

import fr.jadde.fmk.app.assembly.processor.annotation.RootJaddeAnnotation;
import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import org.jboss.weld.bean.ManagedBean;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class JaddeProcessor {

    private final List<JaddeAnnotationProcessor> processors;

    private JaddeProcessor(final List<JaddeAnnotationProcessor> processors) {
        this.processors = Collections.unmodifiableList(processors);
    }

    @SuppressWarnings("rawtypes")
    public void process(final JaddeApplicationContext context) {
        final List<ManagedBean> beans = context.container().resolveAll();
        beans.parallelStream().forEach(bean -> this.processInstance(bean, processors));
    }

    private void processInstance(final ManagedBean<?> bean, final List<JaddeAnnotationProcessor> processors) {
        final List<Annotation> annotations = this.resolveJaddeAnnotations(bean);
        annotations.parallelStream().forEach(annotation -> {
            processors.parallelStream()
                    .filter(processor -> processor.doesSupport(annotation.annotationType(), bean))
                    .forEach(processor -> processor.process(annotation, bean));
        });
    }

    private List<Annotation> resolveJaddeAnnotations(final ManagedBean<?> bean) {
        return Stream.of(bean.getBeanClass().getAnnotations()).filter(annotation -> {
            return annotation.annotationType().isAnnotationPresent(RootJaddeAnnotation.class);
        }).toList();
    }

    public static JaddeProcessor create(final List<JaddeAnnotationProcessor> processors) {
        return new JaddeProcessor(processors);
    }
    
}
