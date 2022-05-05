package fr.jadde.fmk.bundle.security.api.defaultimpl;

import fr.jadde.fmk.bundle.security.annotation.SecurityConfiguration;
import fr.jadde.fmk.bundle.security.api.AuthorizationRoleIntrospect;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SecurityConfiguration
public class KeycloakRoleInstrospector implements AuthorizationRoleIntrospect {

    @Override
    public Set<String> introspect(User user) {
        if (null == user) {
            return Collections.emptySet();
        }
        final JsonObject principal = user.principal();
        if (null == principal) {
            return Collections.emptySet();
        }

        if (principal.containsKey("realm_access") && principal.containsKey("resource_access")) {
            final JsonArray roles = principal.getJsonObject("realm_access").getJsonArray("roles");
            if (null == roles) {
                return Collections.emptySet();
            }
            return this.extractResourcesRoles(principal.getJsonObject("resource_access"), roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toSet()));
        }


        return Collections.emptySet();
    }

    private Set<String> extractResourcesRoles(final JsonObject resources, final Set<String> extractedRoles) {
        final Set<String> resourcesRoles = resources
                .fieldNames().stream()
                .flatMap(fieldName -> {
                    final JsonObject resource = resources.getJsonObject(fieldName);
                    if(resource.containsKey("roles")) {
                        return resource.getJsonArray("roles").stream();
                    }
                    return Stream.empty();
                })
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toSet());

        resourcesRoles.addAll(extractedRoles);

        return resourcesRoles;
    }

}
