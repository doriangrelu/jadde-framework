package fr.jadde.fmk.app.executor.bean.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

public interface BeanWithContext {

    void setContext(final JaddeApplicationContext context);

}
