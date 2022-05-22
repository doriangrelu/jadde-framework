package fr.jadde.test.fmk.bundle.web;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.web.api.MiddlewareProcessor;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.fmk.tests.http.TestHttpClient;
import fr.jadde.test.fmk.bundle.web.mock.FakeApplication;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class JaddeWebAnnotationProcessorTest extends AbstractJaddeTest {

    @Test
    void shouldProcessAWebService(final Vertx vertx, final VertxTestContext testContext) {
        this.log("Start fake application test");
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final TestHttpClient testClient = this.httpClient(context);

        context.container().resolve(MiddlewareProcessor.class).orElseThrow().registerRoute(routingContext -> {
            System.out.println("New call for route '" + routingContext.normalizedPath() + "'");
        });

        context.container().resolve(MiddlewareProcessor.class).orElseThrow().registerInvoker((delegate, invoker, args) -> {
            System.out.println("New call for invoker " + delegate.getClass().getSimpleName() + " --> '" + invoker.getName() + "'");
        });

        testClient.request(HttpMethod.GET, "/my-root/hello/dorian")
                .send().onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(200);
                    Assertions.assertThat(responseAsync.result().body().toJsonObject().getString("text")).isEqualTo("hello dorian");
                });

        testClient.request(HttpMethod.POST, "/my-root/my-path/dodo/jean")
                .send(new JsonObject().put("name", "michel"))
                .onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(200);
                    final JsonObject body = responseAsync.result().body().toJsonObject();
                    Assertions.assertThat(body.getString("name")).isEqualTo("michel");
                    Assertions.assertThat(body.getString("id")).isEqualTo("dodo");
                    Assertions.assertThat(body.getString("uuid")).isEqualTo("jean");
                });
    }

}
