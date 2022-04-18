package fr.jadde.fmk.app.assembly;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import io.vertx.core.Vertx;

public class JaddeApplicationAssembly {

    private final JaddeApplicationContext context;

    public JaddeApplicationAssembly(JaddeApplicationContext context) {
        this.context = context;
    }

    public JaddeApplicationContext context() {
        return context;
    }

    public static JaddeApplicationAssembly create(Class<?> applicationClassName) {
        return new JaddeApplicationAssembly(JaddeApplicationContext.create(applicationClassName, Vertx.vertx()));
    }

}
