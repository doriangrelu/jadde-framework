package fr.jadde.test.fmk.bundle.dispatcher;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.dispatcher.api.JaddeHandlerToolbox;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.test.fmk.bundle.dispatcher.mock.FakeApplication;
import fr.jadde.test.fmk.bundle.dispatcher.mock.services.FirstFakeHandler;
import fr.jadde.test.fmk.bundle.dispatcher.mock.services.SecondFakeHandler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;


class JaddeEventDispatcherTest extends AbstractJaddeTest {

    @AfterEach
    public void tearDown() {
        FirstFakeHandler.message = null;
        SecondFakeHandler.message = null;
    }

    @Test
    void shouldDispatchTopicEventCorrectly(Vertx vertx, VertxTestContext testContext) {
        this.log("Start fake application test");
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final Optional<JaddeHandlerToolbox> toolbox = context.container().resolve(JaddeHandlerToolbox.class);

        Assertions.assertThat(toolbox).isNotEmpty().containsInstanceOf(JaddeHandlerToolbox.class);

        toolbox.get().dispatchTopicEvent("myEvent", null, "{name: \"dorian\"}");

        vertx.setTimer(500, aLong -> {
            testContext.verify(() -> {
                Assertions.assertThat(FirstFakeHandler.message).isEqualTo("{name: \"dorian\"}");
                Assertions.assertThat(SecondFakeHandler.message).isEqualTo("{name: \"dorian\"}");
            });
            testContext.completeNow();
        });

    }

    @Test
    void shouldDispatchQueueEventCorrectly(Vertx vertx, VertxTestContext testContext) {
        this.log("Start fake application test");
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final Optional<JaddeHandlerToolbox> toolbox = context.container().resolve(JaddeHandlerToolbox.class);

        Assertions.assertThat(toolbox).isNotEmpty().containsInstanceOf(JaddeHandlerToolbox.class);

        toolbox.get().dispatchQueueEvent("myEvent", null, "{name: \"dorian\"}");

        vertx.setTimer(500, aLong -> {
            testContext.verify(() -> {
                Assertions.assertThat("{name: \"dorian\"}".equals(FirstFakeHandler.message) ^ "{name: \"dorian\"}".equals(SecondFakeHandler.message)).isTrue();
            });
            testContext.completeNow();
        });

    }

}
