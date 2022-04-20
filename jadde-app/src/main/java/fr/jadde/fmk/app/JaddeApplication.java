package fr.jadde.fmk.app;

import fr.jadde.fmk.app.assembly.JaddeApplicationAssembly;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.container.JaddeContainer;
import fr.jadde.fmk.container.module.AbstractJaddeModule;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class JaddeApplication {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplication.class);

    public static void start(Class<? extends AbstractJaddeModule> targetApplication, String[] arguments) {
        final JaddeContainer container = JaddeContainer.create();
        container.<JaddeApplicationAssembly>tryResolve(JaddeApplicationAssembly.class)
                .ifPresentOrElse(assembly -> {
                    logger.info("Successfully loaded Jadde application container for '" + targetApplication + "'");
                    final JaddeApplicationContext context = JaddeApplicationContext.create(targetApplication, Vertx.vertx())
                            .withArguments(arguments);
                    assembly.withContext(context);
                }, () -> {
                    logger.error("Cannot load application assembly from Jadde container");
                    throw new IllegalStateException("Cannot start application, because cannot load application assembly");
                });
    }

}
