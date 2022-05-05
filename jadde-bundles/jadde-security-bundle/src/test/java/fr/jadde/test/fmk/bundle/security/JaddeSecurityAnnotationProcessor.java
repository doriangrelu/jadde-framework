package fr.jadde.test.fmk.bundle.security;

import com.tngtech.keycloakmock.api.KeycloakMock;
import com.tngtech.keycloakmock.api.ServerConfig;
import com.tngtech.keycloakmock.api.TokenConfig;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.fmk.tests.http.TestHttpClient;
import fr.jadde.test.fmk.bundle.security.mock.FakeApplication;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;


class JaddeSecurityAnnotationProcessor extends AbstractJaddeTest {

    private KeycloakMock keycloakMock;

    @BeforeEach
    public void setUp() {
        this.keycloakMock =  new KeycloakMock(ServerConfig
                .aServerConfig()
                .withPort(8080)
                .withDefaultRealm("jadde-framework")
                .withDefaultHostname("127.0.0.1")
                .build());
        this.keycloakMock.start();
    }

    @AfterEach
    public void tearDown() {
        this.keycloakMock.stop();
    }

    @Test
    void shouldRejectNonAuthenticatedRequest(Vertx vertx, VertxTestContext testContext) {
        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final TestHttpClient testClient = this.httpClient(context);

        testClient.request(HttpMethod.GET, "/my-root/hello/dorian")
                .withAuthenticationToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                .send().onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(401);
                });

        testClient.request(HttpMethod.POST, "/my-root/my-path/dodo/jean")
                .send(new JsonObject().put("name", "michel"))
                .onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(401);
                });

    }


    @Test
    void shouldAcceptAnyAuthenticatedRequests(Vertx vertx, VertxTestContext testContext) {
        String accessToken = this.keycloakMock.getAccessToken(TokenConfig.aTokenConfig().withRealmRole("user-admin").build());

        this.setVertxTestContext(testContext);
        final JaddeApplicationContext context = FakeApplication.start(FakeApplication.class, new String[0], vertx);
        final TestHttpClient testClient = this.httpClient(context);

        testClient.request(HttpMethod.GET, "/my-root/hello/dorian")
                .withAuthenticationToken(accessToken)
                .send().onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(401);
                });

        testClient.request(HttpMethod.POST, "/my-root/my-path/dodo/jean")
                .withAuthenticationToken(accessToken)
                .send(new JsonObject().put("name", "michel"))
                .onComplete(responseAsync -> {
                    Assertions.assertThat(responseAsync.result().statusCode()).isEqualTo(401);
                });

    }

}
