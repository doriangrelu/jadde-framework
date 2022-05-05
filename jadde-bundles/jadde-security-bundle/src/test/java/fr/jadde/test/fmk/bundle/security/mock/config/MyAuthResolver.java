package fr.jadde.test.fmk.bundle.security.mock.config;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.security.annotation.SecurityConfiguration;
import fr.jadde.fmk.bundle.security.api.AuthenticationProviderResolver;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;

@SecurityConfiguration
public class MyAuthResolver implements AuthenticationProviderResolver {

    @Override
    public AuthenticationProvider resolve(JaddeApplicationContext context) {
        JsonObject keycloakJson = new JsonObject()
                .put("realm", "jadde-framework")
                .put("auth-server-url", "http://localhost:8080")
                .put("ssl-required", "external")
                .put("resource", "jadde-fmk-test-dev")
                .put("credentials", new JsonObject()
                        .put("secret", "33kB0QfUGtJICHrHoysNjeHWYpS6iqPW"));

        return KeycloakAuth.create(context.vertx(), keycloakJson);
    }

    @Override
    public String getName() {
        return "KeycloakResolver";
    }

}
