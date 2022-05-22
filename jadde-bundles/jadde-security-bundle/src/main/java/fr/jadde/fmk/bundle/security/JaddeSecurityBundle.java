package fr.jadde.fmk.bundle.security;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;
import fr.jadde.fmk.bundle.security.verticle.JaddeAuthenticationBearerVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.web.client.OAuth2WebClientOptions;

public class JaddeSecurityBundle extends AbstractJaddeBundle {
    public static final String NAME = "jadde.security.bundle";

    @Override
    public boolean next(JaddeApplicationContext context) {
        context.deploy(JaddeAuthenticationBearerVerticle.class);
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public short priorityOrder() {
        return -2;
    }
}
