package fr.jadde.test.fmk.app.mock.middleware;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.middleware.api.AbstractJaddeApplicationMiddleware;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirstMiddleware extends AbstractJaddeApplicationMiddleware {
    @Override
    public boolean next(JaddeApplicationContext context) {
        return this.handleNext(context);
    }
}
