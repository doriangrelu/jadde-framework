package fr.jadde.test.fmk.app.mock.bundle;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.bundle.api.AbstractJaddeApplicationBundle;

public class SecondBundle extends AbstractJaddeApplicationBundle {
    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return "Second";
    }
}
