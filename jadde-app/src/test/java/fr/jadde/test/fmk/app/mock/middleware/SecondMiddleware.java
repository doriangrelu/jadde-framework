package fr.jadde.test.fmk.app.mock.middleware;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.middleware.api.AbstractJaddeApplicationMiddleware;

public class SecondMiddleware extends AbstractJaddeApplicationMiddleware {
    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }
}
