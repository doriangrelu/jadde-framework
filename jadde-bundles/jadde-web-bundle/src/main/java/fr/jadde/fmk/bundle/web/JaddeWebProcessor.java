package fr.jadde.fmk.bundle.web;

import fr.jadde.fmk.app.assembly.processor.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.bundle.web.annotation.RestController;

/**
 * Processes the web beans
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeWebProcessor extends AbstractJaddeBeanProcessor {

    @Override
    public void process(Object target) {

    }

    @Override
    public boolean doesSupport(Object target) {
        return null != target && target.getClass().isAnnotationPresent(RestController.class);
    }

}
