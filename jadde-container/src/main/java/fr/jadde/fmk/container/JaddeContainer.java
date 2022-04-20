package fr.jadde.fmk.container;

import fr.jadde.fmk.container.annotation.JaddeModule;
import org.jboss.weld.bean.ManagedBean;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public class JaddeContainer {

    private final WeldContainer container;

    private JaddeContainer(WeldContainer container) {
        this.container = container;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(final Annotation... annotations) {
        return (T) this.container.select(annotations).get();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> tryResolve(final Annotation... annotations) {
        return Optional.ofNullable(this.resolve(annotations));
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(final Class<?> className, final Annotation... annotations) {
        return (T) this.container.select(className, annotations).get();
    }


    public <T> Optional<T> tryResolve(final Class<?> className, final Annotation... annotations) {
        return Optional.ofNullable(this.resolve(className, annotations));
    }

    public <T> List<T> resolveAll(final Class<T> targetClassName) {
        return this.container.select(targetClassName).stream().toList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ManagedBean> resolveAll() {
        return this.container.getBeanManager().getBeans(Object.class)
                .stream()
                .filter(ManagedBean.class::isInstance)
                .map(ManagedBean.class::cast)
                .filter(bean -> !bean.getBeanClass().isAnnotationPresent(JaddeModule.class))
                .toList();
    }

    public static JaddeContainer create() {
        final Weld weld = new Weld()
                .enableDiscovery()
                .setBeanDiscoveryMode(BeanDiscoveryMode.ANNOTATED)
                .scanClasspathEntries();
        return new JaddeContainer(weld.initialize());
    }

}
