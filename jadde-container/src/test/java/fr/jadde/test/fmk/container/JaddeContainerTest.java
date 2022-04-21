package fr.jadde.test.fmk.container;

import fr.jadde.fmk.container.JaddeContainer;
import fr.jadde.test.fmk.container.mock.MyAnnotation;
import fr.jadde.test.fmk.container.mock.MyApplictionScopedService;
import fr.jadde.test.fmk.container.mock.NotABean;
import fr.jadde.test.fmk.container.mock.StatelessBean;
import org.assertj.core.api.Assertions;
import org.jboss.weld.bean.ManagedBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * @author Dorian GRELU
 */
class JaddeContainerTest {

    private static JaddeContainer container;

    @BeforeAll
    public static void setUp() {
        container = JaddeContainer.create();
    }

    @Test
    @SuppressWarnings("rawtypes")
    void shouldResolvesAllBeans() {
        final List<ManagedBean> managedBeans = container.resolveAllBeans();
        Assertions.assertThat(managedBeans).isNotEmpty();
        Assertions.assertThat(managedBeans.stream().map(ManagedBean::getBeanClass).toList())
                .contains(StatelessBean.class, MyApplictionScopedService.class)
                .doesNotContain(NotABean.class);
    }


    @Test
    void shouldResolvesSingleBean() {
        Assertions.assertThat(container.tryResolve(MyApplictionScopedService.class)).isNotEmpty();
        Assertions.assertThat(container.tryResolve(StatelessBean.class)).isNotEmpty();
        Assertions.assertThat(container.tryResolve(NotABean.class)).isEmpty();

        Assertions.assertThat(container.<Object>resolve(MyApplictionScopedService.class)).isNotNull();
        Assertions.assertThat(container.<Object>resolve(StatelessBean.class)).isNotNull();
        Assertions.assertThat(container.<Object>resolve(NotABean.class)).isNull();

        final Optional<MyApplictionScopedService> instance = container.resolveRealInstance(MyApplictionScopedService.class);
        Assertions.assertThat(instance).isNotEmpty().containsInstanceOf(MyApplictionScopedService.class);
        Assertions.assertThat(instance.get().getClass().isAnnotationPresent(MyAnnotation.class)).isTrue();

    }


}
