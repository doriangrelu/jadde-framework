package fr.jadde.fmk.bundle.web;

import fr.jadde.fmk.app.bundle.api.AbstractJaddeApplicationBundle;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.web.verticle.JaddeApplicationServerVerticle;

public class JaddeWebStarterBundle extends AbstractJaddeApplicationBundle {

    public static final String JADDE_WEB_BUNDLE = "jadde.web.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        context.deploy(JaddeApplicationServerVerticle.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Running Shutdown Hook");
            context.unDeploy(JaddeApplicationServerVerticle.class);
        }));

        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return JADDE_WEB_BUNDLE;
    }

    @Override
    public short priorityOrder() {
        return 2;
    }

}
