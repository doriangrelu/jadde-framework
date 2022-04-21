package fr.jadde.fmk.bundle.web;

import fr.jadde.fmk.app.assembly.processor.api.AbstractJaddeAnnotationProcessor;
import fr.jadde.fmk.bundle.web.annotation.RestController;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Processes the web beans
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
@ApplicationScoped
public class JaddeWebAnnotationProcessor extends AbstractJaddeAnnotationProcessor {
    @Override
    public void process(Object target) {

    }

    @Override
    public boolean doesSupport(Object target) {
        return null != target && target.getClass().isAnnotationPresent(RestController.class);
    }
}
