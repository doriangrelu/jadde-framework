package fr.jadde.fmk.app;

import fr.jadde.fmk.app.assembly.JaddeApplicationAssembly;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.exception.CannotStartApplicationException;
import fr.jadde.fmk.container.JaddeContainer;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

/**
 * Allows you to launch a Jadde application.
 * Will automatically trigger the IoC container and the deployment of Verticles (VertX).
 * <p>
 * The system works with processors that will wrap some actions and configurations
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeApplication {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplication.class);

    public static void start(final Class<? extends JaddeApplication> targetApplication, final String[] arguments) {
        final JaddeContainer container = JaddeContainer.create();

        logger.info("Try setup context registered in application container");
        container.<JaddeApplicationContext>tryResolve(JaddeApplicationContext.class)
                .orElseThrow(() -> cannotStartError("Missing required context"))
                .withApplicationClassName(targetApplication)
                .withContainer(container)
                .withVertX(Vertx.vertx())
                .withArguments(arguments)
                .finalise();

        logger.info("Try start application assembly");
        container.<JaddeApplicationAssembly>tryResolve(JaddeApplicationAssembly.class)
                .orElseThrow(() -> cannotStartError("Cannot start application, because cannot load application assembly"))
                .processAssembly();
    }

    private static RuntimeException cannotStartError(final String message) {
        return new CannotStartApplicationException(message);
    }


}
