package fr.jadde.test.fmk.container;

import fr.jadde.fmk.container.JaddeContainer;
import fr.jadde.test.fmk.container.mock.A;
import fr.jadde.test.fmk.container.mock.B;
import fr.jadde.test.fmk.container.mock.C;
import fr.jadde.test.fmk.container.mock.I;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Dorian GRELU
 */
class JaddeContainerTest {


    @Test
    void shouldResolvesSingleBean() {
        final JaddeContainer container = new JaddeContainer();

        Assertions.assertThat(container.getInstance(A.class))
                .isNotNull()
                .isInstanceOf(A.class);

        Assertions.assertThat(container.resolve(A.class))
                .isNotEmpty()
                .containsInstanceOf(A.class);

        container.registerInstance(B.class);
        container.registerInstance(C.class);

        Assertions.assertThat(container.resolve(B.class))
                .isNotEmpty()
                .containsInstanceOf(B.class);
        Assertions.assertThat(container.resolve(C.class))
                .isNotEmpty()
                .containsInstanceOf(C.class);

        container.resolve(I.class);


//        final Optional<MyApplictionScopedService> instance = container.resolve(MyApplictionScopedService.class);
//        Assertions.assertThat(instance).isNotEmpty().containsInstanceOf(MyApplictionScopedService.class);
//        Assertions.assertThat(instance.get().getClass().isAnnotationPresent(MyAnnotation.class)).isTrue();

    }


}
