package fr.jadde.fmk.bundle.dispatcher;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;

public class JaddeDispatcherBundle extends AbstractJaddeBundle {

    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return null;
    }

}
