package fr.jadde.fmk.bundle.validation;

import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.bundle.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

@Slf4j
public class JaddeValidationProcessor extends AbstractJaddeBeanProcessor {



    @Override
    public void process(Object target) {
        Stream.of(target.getClass().getMethods()).forEach(method -> {
            method.


        });
    }

    @Override
    public boolean doesSupport(Object target) {
        return null != target && AnnotationUtils.isAnnotationPresent(target.getClass(), Validated.class);
    }

}
