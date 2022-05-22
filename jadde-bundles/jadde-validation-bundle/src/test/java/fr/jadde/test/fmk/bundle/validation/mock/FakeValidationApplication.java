package fr.jadde.test.fmk.bundle.validation.mock;

import fr.jadde.fmk.app.JaddeApplication;

/**
 * @author Dorian GRELU
 */
public class FakeValidationApplication extends JaddeApplication {

    public static void main(String[] args) {
        JaddeApplication.start(FakeValidationApplication.class, args);
    }

}
