package fr.jadde.fmk.container;

import fr.jadde.fmk.container.annotation.JaddeModule;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import org.jboss.weld.bean.ManagedBean;
import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.exceptions.UnsatisfiedResolutionException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * Allows you to provide a dependency injector container
 *
 * @author Dorian GRELU
 * @version Avril. 2022
 */
public class JaddeContainer {

    private static final Logger logger = LoggerFactory.getLogger(JaddeContainer.class);

    private final WeldContainer container;

    /**
     * Ctor.
     *
     * @param container the Weld container instance
     */
    private JaddeContainer(WeldContainer container) {
        this.container = container;
    }

    /**
     * Resolves bean from annotation
     *
     * @param annotations target annotations array
     * @param <T>         expected generic bean type
     * @return the target bean if exists or null
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Annotation... annotations) {
        return (T) this.container.select(annotations).get();
    }

    /**
     * Tries to resolve bean from annotation
     *
     * @param annotations target annotations array
     * @param <T>         expected generic bean type
     * @return optional target bean
     */
    public <T> Optional<T> tryResolve(final Annotation... annotations) {
        return Optional.ofNullable(this.resolve(annotations));
    }

    /**
     * Resolves bean from annotation and class name
     *
     * @param className   expected class name
     * @param annotations target annotations array
     * @param <T>         expected generic bean type
     * @return the target bean if exists or null
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(final Class<?> className, final Annotation... annotations) {
        try {
            return (T) this.container.select(className, annotations).get();
        } catch (UnsatisfiedResolutionException e) {
            logger.warn("Cannot resolve bean '" + className + "'", e);
            return null;
        }
    }

    /**
     * Resolves bean from annotation and class name
     *
     * @param className   target class name
     * @param annotations target annotations array
     * @param <T>         expected generic bean type
     * @return optional bean
     */
    public <T> Optional<T> tryResolve(final Class<?> className, final Annotation... annotations) {
        return Optional.ofNullable(this.resolve(className, annotations));
    }

    /**
     * Resolves all beans from class name
     *
     * @param targetClassName the target class name
     * @param <T>             expected generic beans type
     * @return the list that contains all beans
     */
    public <T> List<T> resolveAll(final Class<T> targetClassName) {
        return this.container.select(targetClassName).stream().toList();
    }

    /**
     * Provides real instance resolving
     * The Jadde container return by default a proxy object
     * This method bypass the proxy object
     * In most cases you must use the proxy object
     *
     * @param targetClassName target class name
     * @param <T>             expected generic beans type
     * @return optional real instance
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> resolveRealInstance(final Class<?> targetClassName) {
        final CreationalContext<T> ctx = this.container.getBeanManager().createCreationalContext(null);
        final List<Bean<?>> types = this.container.getBeanManager().getBeans(targetClassName).stream().toList();

        if (types.isEmpty()) {
            return Optional.empty();
        }
        final Bean<T> bean = (Bean<T>) types.get(0);

        return Optional.ofNullable(this.container.getBeanManager().getContext(bean.getScope()).get(bean, ctx));
    }

    /**
     * Resolves all managed beans
     *
     * @return the managed beans list
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ManagedBean> resolveAllBeans() {
        return this.container.getBeanManager().getBeans(Object.class)
                .stream()
                .filter(ManagedBean.class::isInstance)
                .map(ManagedBean.class::cast)
                .filter(bean -> !bean.getBeanClass().isAnnotationPresent(JaddeModule.class))
                .toList();
    }

    /**
     * Jadde container creator !
     *
     * @return the Jadde container himself !
     */
    public static JaddeContainer create() {
        final Weld weld = new Weld()
                .enableDiscovery()
                .setBeanDiscoveryMode(BeanDiscoveryMode.ANNOTATED)
                .scanClasspathEntries();
        return new JaddeContainer(weld.initialize());
    }

}
