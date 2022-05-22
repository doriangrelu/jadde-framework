package fr.jadde.test.fmk.bundle.validation;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.validation.domain.WebValidationError;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.fmk.tests.http.TestHttpClient;
import fr.jadde.test.fmk.bundle.validation.mock.FakeValidationApplication;
import fr.jadde.test.fmk.bundle.validation.mock.domain.MyEntity;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class JaddeValidationTest extends AbstractJaddeTest {

    @Test
    void shouldProcessAWebService(final Vertx vertx, final VertxTestContext testContext) {
        this.log("Start fake application test");
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeValidationApplication.start(FakeValidationApplication.class, new String[0], vertx);
        final TestHttpClient testClient = this.httpClient(context);

        final MyEntity validEntity = this.build("dorian", "grelu");
        testClient.request(HttpMethod.POST, "/home")
                .send(validEntity)
                .onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(200);
                    Assertions.assertThat(responseAsync.result().body().toJsonObject().mapTo(MyEntity.class)).isEqualTo(validEntity);
                });

        final MyEntity invalidEntity = this.build("dorian6584656584", "");
        testClient.request(HttpMethod.POST, "/home")
                .send(invalidEntity)
                .onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(400);
                    final WebValidationError error = responseAsync.result().body().toJsonObject().mapTo(WebValidationError.class);
                    Assertions.assertThat(error).isNotNull();
                    Assertions.assertThat(error.status()).isEqualTo(400);
                    Assertions.assertThat(error.errors()).hasSize(2);
                });

    }


    private MyEntity build(final String firstname, final String lastname) {
        final MyEntity entity = new MyEntity();
        entity.setFirstname(firstname);
        entity.setLastname(lastname);
        return entity;
    }

}
