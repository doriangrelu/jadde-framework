package fr.jadde.test.fmk.app.mock;

import fr.jadde.fmk.app.JaddeApplication;

public class FakeApplication extends JaddeApplication {

    public static void main(String[] args) {
        JaddeApplication.start(FakeApplication.class, args);
    }

}
