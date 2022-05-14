package fr.jadde.fmk.bundle.validation;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JaddeValidationBundle extends AbstractJaddeBundle {

    public static final String NAME = "jadde.validation.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        log.info("Successfully loaded validation bundle");
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
