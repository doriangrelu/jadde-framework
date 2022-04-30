package fr.jadde.fmk.app.executor.bean.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;

public abstract class AbstractBeanWithContext implements BeanWithContext {

    private JaddeApplicationContext context;

    @Override
    public void setContext(JaddeApplicationContext context) {
        this.context = context;
    }

    protected JaddeApplicationContext context() {
        return this.context;
    }

}
