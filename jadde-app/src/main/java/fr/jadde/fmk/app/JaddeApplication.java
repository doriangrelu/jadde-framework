package fr.jadde.fmk.app;

import fr.jadde.fmk.app.assembly.JaddeApplicationAssembly;
import fr.jadde.fmk.app.context.tools.ClasspathResolver;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.container.api.JaddeContainer;
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

    public static JaddeApplicationContext start(final Class<? extends JaddeApplication> targetApplication, final String[] arguments, final Vertx vertx) {
        logger.info("Try setup to context");
        final JaddeApplicationContext context = new JaddeApplicationContext()
                .withApplicationClassName(targetApplication)
                .withContainer(new JaddeContainer())
                .withVertX(vertx)
                .withClasspathResolver(ClasspathResolver.create())
                .withArguments(arguments);

        logger.info("Try to start application assembly");
        final JaddeApplicationAssembly assembly = new JaddeApplicationAssembly();
        assembly.processAssembly(context);

        logger.info("Successfully assembled Jadde application");

        return context;
    }

    public static JaddeApplicationContext start(final Class<? extends JaddeApplication> targetApplication, final String[] arguments) {
        return start(targetApplication, arguments, Vertx.vertx());
    }

}
