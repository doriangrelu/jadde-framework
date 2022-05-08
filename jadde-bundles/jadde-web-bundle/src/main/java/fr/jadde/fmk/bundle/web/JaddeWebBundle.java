package fr.jadde.fmk.bundle.web;

import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.web.verticle.JaddeApplicationServerVerticle;

public class JaddeWebBundle extends AbstractJaddeBundle {

    public static final String NAME = "jadde.web.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        context.deploy(JaddeApplicationServerVerticle.class);
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public short priorityOrder() {
        return 2;
    }

}
