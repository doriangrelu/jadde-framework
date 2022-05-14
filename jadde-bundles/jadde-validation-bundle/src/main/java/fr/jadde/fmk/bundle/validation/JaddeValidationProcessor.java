package fr.jadde.fmk.bundle.validation;

import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.bundle.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaddeValidationProcessor extends AbstractJaddeBeanProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JaddeValidationProcessor.class);

    @Override
    public void process(Object target) {

    }

    @Override
    public boolean doesSupport(Object target) {
        return null != target && AnnotationUtils.isAnnotationPresent(target.getClass(), Validated.class);
    }

}
