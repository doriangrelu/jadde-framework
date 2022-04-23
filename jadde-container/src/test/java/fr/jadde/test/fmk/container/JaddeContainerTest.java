package fr.jadde.test.fmk.container;

import fr.jadde.fmk.container.JaddeContainer;
import fr.jadde.fmk.container.exception.NotSingleBeanException;
import fr.jadde.test.fmk.container.mock.*;
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

        Assertions.assertThat(container.resolve(I.class))
                .isNotNull()
                .isNotEmpty()
                .containsInstanceOf(B.class);

        Assertions.assertThat(container.resolve(I.class, "this"))
                .isNotNull()
                .isNotEmpty()
                .containsInstanceOf(C.class);

        container.registerInstance(Conflict1.class);
        container.registerInstance(Conflict2.class);

        Assertions.assertThatThrownBy(() -> container.resolve(II.class)).isInstanceOf(NotSingleBeanException.class);

    }


}
