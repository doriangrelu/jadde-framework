package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.assembly.processor.JaddeProcessor;
import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.container.annotation.JaddeModule;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
@JaddeModule
public class JaddeApplicationAssembly {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationAssembly.class);

    @Inject
    private JaddeApplicationContext context;

    public void processAssembly() {
        if (null == context) {
            throw new IllegalStateException("Cannot start assembly, missing context");
        }
        final List<JaddeAnnotationProcessor> processors = context.container().resolveAll(JaddeAnnotationProcessor.class);
        logger.info("'" + processors.size() + "' founded, start application processing");
        logger.debug("Processor list -> " + Json.encode(processors));

        final JaddeProcessor processor = JaddeProcessor.create(processors);
        processor.process(this.context);
    }

}
