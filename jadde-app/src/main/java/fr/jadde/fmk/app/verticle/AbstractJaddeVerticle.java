package fr.jadde.fmk.app.verticle;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import io.vertx.core.AbstractVerticle;

public class AbstractJaddeVerticle extends AbstractVerticle {

    private JaddeApplicationContext context;

    public AbstractJaddeVerticle withContext(final JaddeApplicationContext context) {
        this.context = context;
        return this;
    }

    protected JaddeApplicationContext context() {
        if (null == context) {
            throw new IllegalStateException("Missing required config in Jadde verticle");
        }
        return this.context;
    }

}
