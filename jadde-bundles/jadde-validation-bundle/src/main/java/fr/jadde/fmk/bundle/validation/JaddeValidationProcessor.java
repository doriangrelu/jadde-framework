package fr.jadde.fmk.bundle.validation;

import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.bundle.validation.annotation.Validated;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class JaddeValidationProcessor extends AbstractJaddeBeanProcessor {

    @Override
    public void process(final Object target) {
        final Class<?> targetClass = target.getClass();
        final Method[] methods = targetClass.getMethods();

        Stream.of(methods).forEach(method -> {

        });
    }

    private void processArgument(final Object target, final Method method) {
        final Parameter[] parameters = method.getParameters();
        final List<Parameter> needValidationParameters = Stream.of(parameters)
                .filter(parameter -> AnnotationUtils.isAnnotationPresent(parameter, Valid.class))
                .toList();
    }

    @Override
    public boolean doesSupport(final Object target) {
        return null != target && AnnotationUtils.isAnnotationPresent(target.getClass(), Validated.class);
    }

}
