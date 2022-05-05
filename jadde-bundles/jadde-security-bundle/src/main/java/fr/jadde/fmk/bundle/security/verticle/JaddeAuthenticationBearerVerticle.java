package fr.jadde.fmk.bundle.security.verticle;

import fr.jadde.fmk.app.context.configuration.Parameter;
import fr.jadde.fmk.app.verticle.AbstractJaddeVerticle;
import fr.jadde.fmk.bundle.security.api.AuthenticationProviderResolver;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JaddeAuthenticationBearerVerticle extends AbstractJaddeVerticle {

    public static final Logger logger = LoggerFactory.getLogger(JaddeAuthenticationBearerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        final List<AuthenticationProviderResolver> resolvers = this.context().container().resolveAll(AuthenticationProviderResolver.class);
        final Set<AuthenticationProvider> providers = resolvers.stream()
                .map(authenticationProviderResolver -> authenticationProviderResolver.resolveAll(this.context()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        logger.info("Found {} providers from resolvers: {}", providers.size(), resolvers.stream().map(AuthenticationProviderResolver::getName).toArray());
        this.context().withParameter(Parameter.AUTH_PROVIDER.parameterName(), providers);
        startPromise.complete();

//        this.context()
//                .configuration().authenticationConfiguration().onSuccess(config -> {
//                    if (config.containsKey("clientId") && config.containsKey("secret") && config.containsKey("host")) {
//                        logger.info("Deploy oauth provider");
//
//
//                        JsonObject keycloakJson = new JsonObject()
//                                .put("realm", "jadde-framework")
//                                .put("auth-server-url", "http://localhost:8080")
//                                .put("ssl-required", "external")
//                                .put("resource", "jadde-fmk-test-dev")
//                                .put("credentials", new JsonObject()
//                                        .put("secret", "33kB0QfUGtJICHrHoysNjeHWYpS6iqPW"));
//
//                        OAuth2Auth authProvider = KeycloakAuth.create(vertx, keycloakJson);
//
//                        KeycloakAuth.discover(
//                                        vertx,
//                                        new OAuth2Options()
//                                                .setClientId("jadde-fmk-test-dev")
//                                                .setClientSecret("33kB0QfUGtJICHrHoysNjeHWYpS6iqPW")
//                                                .setSite("http://127.0.0.1:8080/realms/{realm}")
//                                                .setTenant("jadde-framework"))
//                                .onSuccess(oauth2 -> {
//
//                                });
//
//
//                    } else {
//                        logger.warn("Cannot register authentication provider");
//                    }
//                    startPromise.complete();
//                });
    }

}
