package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.assembly.processor.JaddeGenericProcessorResolver;
import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.exception.PackageScanningException;
import fr.jadde.fmk.app.reflection.JaddePackageScanner;
import fr.jadde.fmk.container.annotation.JaddeModule;
import fr.jadde.fmk.container.module.AbstractJaddeModule;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@JaddeModule
public class JaddeApplicationAssembly {

    private JaddeApplicationContext context;

    private final Set<JaddeAnnotationProcessor> processors;

    public JaddeApplicationAssembly() {
        this.processors = new HashSet<>();
    }

    public JaddeApplicationAssembly withContext(final JaddeApplicationContext context) {
        this.context = context;
        return this;
    }

    public JaddeApplicationAssembly withProcessor(final JaddeAnnotationProcessor processor) {
        processor.setContext(this.context);
        this.processors.add(processor);
        return this;
    }

    public JaddeApplicationContext context() {
        return context;
    }

    public void start() {
        final JaddeGenericProcessorResolver processorResolver = new JaddeGenericProcessorResolver(this.context, this.processors);
        try {
            processorResolver.handleResolve(JaddePackageScanner.scan(this.context.applicationClassName()));
        } catch (PackageScanningException e) {
            e.printStackTrace();
        }
    }

}
