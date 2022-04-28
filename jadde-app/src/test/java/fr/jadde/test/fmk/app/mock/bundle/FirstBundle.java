package fr.jadde.test.fmk.app.mock.bundle;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;

public class FirstBundle extends AbstractJaddeBundle {

    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return "First";
    }
    
}
