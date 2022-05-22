package fr.jadde.fmk.app.executor.bean.tools;

import fr.jadde.fmk.app.executor.bean.api.JaddeBeanProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.container.annotation.ExcludeBeanProcessing;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Dorian GRELU
 */
public final class BeanUtils {

    public static List<Object> getSafeBeans(final JaddeApplicationContext context, final Class<?>... excludes) {
        final List<Object> beans = context.container().resolveAll();
        return beans.stream()
                .filter(bean -> !(JaddeBeanProcessor.class.isAssignableFrom(bean.getClass())))
                .filter(bean -> !(JaddeApplicationContext.class.isAssignableFrom(bean.getClass())))
                .filter(bean -> !AnnotationUtils.isAnnotationPresent(bean.getClass(), ExcludeBeanProcessing.class))
                .filter(bean -> exclude(bean, excludes))
                .toList();
    }

    private static boolean exclude(final Object bean, final Class<?>... excludes) {
        return Stream.of(excludes).noneMatch(exclude -> exclude.isAssignableFrom(bean.getClass()));
    }

}
