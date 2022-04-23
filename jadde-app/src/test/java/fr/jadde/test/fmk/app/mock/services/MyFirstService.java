package fr.jadde.test.fmk.app.mock.services;

import fr.jadde.fmk.container.annotation.JaddeBean;
import fr.jadde.test.fmk.app.mock.annotation.MyAnnot;

/**
 * @author Dorian GRELU
 */
@MyAnnot
@JaddeBean
public class MyFirstService  {

    private static String containerIdentifier;

    public static String containerIdentifier() {
        return containerIdentifier;
    }

    public MyFirstService setContainerIdentifier(String value) {
        containerIdentifier = value;
        return this;
    }
}
