package fr.jadde.test.fmk.bundles.openapi.mock;

import fr.jadde.fmk.app.JaddeApplication;

public class FakeOpenApiApplication extends JaddeApplication {
    public static void main(final String[] args) {
        JaddeApplication.start(FakeOpenApiApplication.class, args);
    }

}
