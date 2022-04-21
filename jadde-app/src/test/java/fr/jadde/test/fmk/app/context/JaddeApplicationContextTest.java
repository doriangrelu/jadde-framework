package fr.jadde.test.fmk.app.context;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.JaddeApplication;
import io.vertx.core.Vertx;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Dorian GRELU
 */
class JaddeApplicationContextTest extends JaddeApplication {

    @Test
    void testCreateContextWithArguments() {
        final var context = new JaddeApplicationContext()
                .withApplicationClassName(JaddeApplicationContextTest.class)
                .withVertX(Vertx.vertx());

        final String[] arguments = Arrays.asList("a=b", "b=b", "c=t.y.u", "d=6000", "...=090..").toArray(new String[0]);

        context.withArguments(arguments);

        Assertions.assertThat(context.applicationClassName()).isEqualTo(JaddeApplicationContextTest.class);
        Assertions.assertThat(context.vertx()).isNotNull().isInstanceOf(Vertx.class);

        Assertions.assertThat(context.argument("a")).isNotEmpty().contains("b");
        Assertions.assertThat(context.argument("b")).isNotEmpty().contains("b");
        Assertions.assertThat(context.argument("c")).isNotEmpty().contains("t.y.u");
        Assertions.assertThat(context.argument("d")).isNotEmpty().contains("6000");
        Assertions.assertThat(context.argument("...")).isEmpty();

        Assertions.assertThat(context.arguments())
                .isEqualTo(
                        Map.of(
                                "a", "b",
                                "b", "b",
                                "c", "t.y.u",
                                "d", "6000"
                        )
                );
    }

}
