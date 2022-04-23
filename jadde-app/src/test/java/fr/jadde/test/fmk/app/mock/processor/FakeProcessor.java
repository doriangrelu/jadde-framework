package fr.jadde.test.fmk.app.mock.processor;

import fr.jadde.fmk.app.assembly.processor.api.AbstractJaddeBeanProcessor;
import fr.jadde.test.fmk.app.mock.annotation.MyAnnot;
import fr.jadde.test.fmk.app.mock.services.MyFirstService;

import java.util.UUID;

/**
 * @author Dorian GRELU
 */
public class FakeProcessor extends AbstractJaddeBeanProcessor {

    @Override
    public void process(Object target) {
        if (target instanceof MyFirstService myFirstService) {
            myFirstService.setContainerIdentifier(UUID.randomUUID().toString());
        }
    }

    @Override
    public boolean doesSupport(Object target) {
        return target.getClass().isAnnotationPresent(MyAnnot.class);
    }

}
