package fr.jadde.test.fmk.app.mock.services;

import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.container.annotation.JaddeBean;
import fr.jadde.test.fmk.app.mock.annotation.MyAnnot;

/**
 * @author Dorian GRELU
 */
@MyAnnot
public class MyFirstService {

    @Inject(qualifier = "myQualifier")
    private MyServiceInterface myService;

    @Inject(required = false)
    private String unresolvableService;

    private static String containerIdentifier;

    public static String containerIdentifier() {
        return containerIdentifier;
    }

    public MyFirstService setContainerIdentifier(String value) {
        containerIdentifier = value;
        return this;
    }

    public String unresolvableService() {
        return this.unresolvableService;
    }

    public MyServiceInterface myService() {
        return this.myService;
    }
}
