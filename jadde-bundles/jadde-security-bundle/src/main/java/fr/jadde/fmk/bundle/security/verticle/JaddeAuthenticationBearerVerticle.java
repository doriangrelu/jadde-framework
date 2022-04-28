package fr.jadde.fmk.bundle.security.verticle;

import fr.jadde.fmk.app.context.configuration.Parameter;
import fr.jadde.fmk.app.verticle.AbstractJaddeVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaddeAuthenticationBearerVerticle extends AbstractJaddeVerticle {

    public static final Logger logger = LoggerFactory.getLogger(JaddeAuthenticationBearerVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        this.context()
                .configuration().authenticationConfiguration().onSuccess(config -> {
                    if (config.containsKey("clientId") && config.containsKey("secret") && config.containsKey("host")) {
                        logger.info("Deploy oauth provider");
                        OAuth2Auth authProvider = OAuth2Auth.create(this.context().vertx(), new OAuth2Options()
                                .setFlow(OAuth2FlowType.CLIENT)
                                .setClientId(config.getString("clientId"))
                                .setClientSecret(config.getString("secret"))
                                .setSite(config.getString("host")));
                        this.context().withParameter(Parameter.AUTH_PROVIDER.parameterName(), authProvider);
                    } else {
                        logger.warn("Cannot register authentication provider");
                    }
                    startPromise.complete();
                });
    }

}
