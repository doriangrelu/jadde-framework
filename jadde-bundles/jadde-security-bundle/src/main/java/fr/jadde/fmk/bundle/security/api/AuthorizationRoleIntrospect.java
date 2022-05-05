package fr.jadde.fmk.bundle.security.api;

import io.vertx.ext.auth.User;

import java.util.Set;

public interface AuthorizationRoleIntrospect {
    Set<String> introspect(final User user);
}
