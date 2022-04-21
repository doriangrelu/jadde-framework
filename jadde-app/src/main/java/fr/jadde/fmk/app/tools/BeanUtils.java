package fr.jadde.fmk.app.tools;

import fr.jadde.fmk.app.assembly.processor.api.JaddeAnnotationProcessor;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import org.jboss.weld.bean.ManagedBean;


import java.util.List;
import java.util.stream.Stream;

/**
 * @author Dorian GRELU
 */
@SuppressWarnings("rawtypes")
public final class BeanUtils {

    private static final String WELD_PACKAGE = "org.jboss.weld";

    public static List<ManagedBean> getSafeBeans(final JaddeApplicationContext context, final Class<?>... excludes) {
        final List<ManagedBean> beans = context.container().resolveAllBeans();
        return beans.parallelStream()
                .filter(bean -> !(JaddeAnnotationProcessor.class.isAssignableFrom(bean.getBeanClass())))
                .filter(bean -> !(JaddeApplicationContext.class.isAssignableFrom(bean.getBeanClass())))
                .filter(bean -> !bean.getBeanClass().getName().startsWith(WELD_PACKAGE))
                .filter(bean -> exclude(bean, excludes))
                .toList();
    }

    private static boolean exclude(final ManagedBean bean, final Class<?>... excludes) {
        return Stream.of(excludes).noneMatch(exclude -> exclude.isAssignableFrom(bean.getBeanClass()));
    }

}
