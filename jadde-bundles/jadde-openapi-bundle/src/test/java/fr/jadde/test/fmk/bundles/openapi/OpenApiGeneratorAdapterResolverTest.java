package fr.jadde.test.fmk.bundles.openapi;


import fr.jadde.fmk.app.JaddeApplication;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.test.fmk.bundles.openapi.mock.FakeOpenApiApplication;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;

class OpenApiGeneratorAdapterResolverTest extends AbstractJaddeTest {

    @Test
    void test(final Vertx vertx, final VertxTestContext testContext) {
        final JaddeApplicationContext context = JaddeApplication.start(FakeOpenApiApplication.class, new String[0], vertx);

        testContext.completeNow();
    }

}
