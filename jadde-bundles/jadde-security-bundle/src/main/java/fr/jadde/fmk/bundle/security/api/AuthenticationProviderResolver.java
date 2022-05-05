package fr.jadde.fmk.bundle.security.api;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

import java.util.Set;

public interface AuthenticationProviderResolver {

    AuthenticationProvider resolve(final JaddeApplicationContext context);

    default Set<AuthenticationProvider> resolveAll(final JaddeApplicationContext context) {
        return Set.of(this.resolve(context));
    }

    String getName();

}
