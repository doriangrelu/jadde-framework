package fr.jadde.test.fmk.app.mock.processor;

import fr.jadde.fmk.app.assembly.processor.api.AbstractJaddeAnnotationProcessor;
import fr.jadde.test.fmk.app.mock.annotation.MyAnnot;
import fr.jadde.test.fmk.app.mock.services.MyFirstService;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.annotation.Annotation;
import java.util.UUID;

@ApplicationScoped
public class FakeProcessor extends AbstractJaddeAnnotationProcessor {

    @Override
    public void process(Annotation annotation, Object target) {
        if (target instanceof MyFirstService myFirstService) {
            myFirstService.setContainerIdentifier(UUID.randomUUID().toString());
        }
    }

    @Override
    public boolean doesSupport(Class<? extends Annotation> annotation, Object target) {
        return annotation.equals(MyAnnot.class);
    }

}
