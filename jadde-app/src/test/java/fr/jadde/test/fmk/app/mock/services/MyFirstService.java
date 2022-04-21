package fr.jadde.test.fmk.app.mock.services;

import fr.jadde.test.fmk.app.mock.annotation.MyAnnot;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@MyAnnot
public class MyFirstService {

    private static String containerIdentifier;

    @Inject
    private MyServiceInterface myServiceInterface;

    public static String containerIdentifier() {
        return containerIdentifier;
    }

    public MyFirstService setContainerIdentifier(String value) {
        containerIdentifier = value;
        return this;
    }
}
