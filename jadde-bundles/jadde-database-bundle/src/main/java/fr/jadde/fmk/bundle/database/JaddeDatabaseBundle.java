package fr.jadde.fmk.bundle.database;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;

public class JaddeDatabaseBundle extends AbstractJaddeBundle {

    public static final String NAME = "jadde.database.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
