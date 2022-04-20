package fr.jadde.test.fmk.app.mock;

import fr.jadde.fmk.app.JaddeApplication;
import fr.jadde.fmk.container.module.AbstractJaddeModule;

public class FakeApplication extends AbstractJaddeModule {

    public static void main(String[] args) {
        JaddeApplication.start(FakeApplication.class, args);
    }

}
