package fr.jadde.test.fmk.bundle.database.mock;

import fr.jadde.fmk.app.JaddeApplication;

public class FakeDatabaseApplication extends JaddeApplication {

    public static void main(final String[] args) {
        JaddeApplication.start(FakeDatabaseApplication.class, args);
    }

}
