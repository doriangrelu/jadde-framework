package fr.jadde.fmk.app.tools;

import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Dorian GRELU
 */
public final class BeanUtils {

    public static List<Object> getSafeBeans(final JaddeApplicationContext context, final Class<?>... excludes) {
        final List<Object> beans = context.container().resolveAll();
        return beans.stream()
                .filter(bean -> !(JaddeAnnotationProcessor.class.isAssignableFrom(bean.getClass())))
                .filter(bean -> !(JaddeApplicationContext.class.isAssignableFrom(bean.getClass())))
                .filter(bean -> exclude(bean, excludes))
                .toList();
    }

    private static boolean exclude(final Object bean, final Class<?>... excludes) {
        return Stream.of(excludes).noneMatch(exclude -> exclude.isAssignableFrom(bean.getClass()));
    }

}
