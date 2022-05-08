package fr.jadde.fmk.bundle.dispatcher;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;

public class JaddeDispatcherBundle extends AbstractJaddeBundle {

    public static final String JADDE_DISPATCHER_BUNDLE = "jadde.dispatcher.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return JADDE_DISPATCHER_BUNDLE;
    }

}
