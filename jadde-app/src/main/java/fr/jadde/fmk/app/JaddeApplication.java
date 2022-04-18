package fr.jadde.fmk.app;

import fr.jadde.fmk.app.assembly.JaddeApplicationAssembly;

public class JaddeApplication {

    public static void start(Class<?> targetApplication, String[] arguments) {
        final JaddeApplicationAssembly assembly = JaddeApplicationAssembly.create(targetApplication);
        assembly.context().withArguments(arguments);
    }

}
