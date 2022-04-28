package fr.jadde.test.fmk.bundle.security.mock;

import fr.jadde.fmk.app.JaddeApplication;

/**
 * @author Dorian GRELU
 */
public class FakeApplication extends JaddeApplication {

    public static void main(String[] args) {
        JaddeApplication.start(FakeApplication.class, args);
    }

}
