package fr.jadde.test.fmk.bundle.dispatcher;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.dispatcher.api.JaddeHandlerToolbox;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.test.fmk.bundle.dispatcher.mock.FakeApplication;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class JaddeCommandDispatcherTest extends AbstractJaddeTest {

    @Test
    void shouldDispatchCommandCorrectly(Vertx vertx, VertxTestContext testContext) {
        this.log("Start fake application test");
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final Optional<JaddeHandlerToolbox> toolbox = context.container().resolve(JaddeHandlerToolbox.class);

        Assertions.assertThat(toolbox).isNotEmpty().containsInstanceOf(JaddeHandlerToolbox.class);

        toolbox.get().<String>dispatchCommand("myCommand", null, "my")
                .onSuccess(result -> {
                    testContext.verify(() -> {
                        Assertions.assertThat(result).isEqualTo("myCommand!");
                        testContext.completeNow();
                    });
                }).onFailure(testContext::failNow);
    }


    @Test
    void shouldDispatchCommandErrorCorrectly(Vertx vertx, VertxTestContext testContext) {
        this.log("Start fake application test");
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final Optional<JaddeHandlerToolbox> toolbox = context.container().resolve(JaddeHandlerToolbox.class);

        Assertions.assertThat(toolbox).isNotEmpty().containsInstanceOf(JaddeHandlerToolbox.class);

        toolbox.get().<String>dispatchCommand("myCommandError", null, "my")
                .onSuccess(result -> {
                    testContext.failNow("Succeeded command not expected");
                }).onFailure(throwable -> {
                    testContext.verify(() -> {
                        Assertions.assertThat(throwable).isInstanceOf(ReplyException.class);
                        testContext.completeNow();
                    });
                });
    }

}
