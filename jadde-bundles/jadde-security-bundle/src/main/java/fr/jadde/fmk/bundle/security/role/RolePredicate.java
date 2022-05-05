package fr.jadde.fmk.bundle.security.role;

import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.bundle.security.annotation.authorization.AllRoles;
import fr.jadde.fmk.bundle.security.annotation.authorization.AnyAuthenticated;
import fr.jadde.fmk.bundle.security.annotation.authorization.AnyRole;
import fr.jadde.fmk.bundle.security.annotation.authorization.RolePolicy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Role chain predicate
 *
 * @author Dorian GRELU
 */
public class RolePredicate {

    private final Annotation policy;

    private RolePredicate andOther;

    private RolePredicate(final Annotation policy) {
        this.policy = policy;
    }

    public boolean doesAuthorize(final Set<String> userRoles) {
        final boolean andOtherResult = null == this.andOther || andOther.doesAuthorize(userRoles);

        if (null == policy || policy instanceof AnyAuthenticated) {
            return andOtherResult;
        }

        if (policy instanceof AllRoles allRoles) {
            return userRoles.containsAll(Arrays.asList(allRoles.value())) && andOtherResult;
        }

        if (policy instanceof AnyRole anyRole) {
            final List<String> anyRoles = Arrays.asList(anyRole.value());
            return userRoles.stream().anyMatch(anyRoles::contains) && andOtherResult;
        }

        return andOtherResult;
    }

    public RolePredicate and(final RolePredicate andOther) {
        if (andOther == this) {
            throw new IllegalStateException("Circular role predicate error");
        }
        this.andOther = andOther;
        return this;
    }

    public String policyName() {
        if (policy instanceof AllRoles allRoles) {
            return "ALL_ROLES{" + Arrays.toString(allRoles.value()) + "}";
        }
        if (policy instanceof AnyRole anyRole) {
            return "ANY_ROLE{" + Arrays.toString(anyRole.value()) + "}";
        }
        return "DEFAULT";
    }

    public static RolePredicate of(final Class<?> targetClass) {
        if (AnnotationUtils.isAnnotationPresent(targetClass, RolePolicy.class)) {
            return new RolePredicate(AnnotationUtils.getAnnotation(targetClass, RolePolicy.class).orElse(null));
        }
        return new RolePredicate(null);
    }


    public static RolePredicate of(final Method targetMethod) {
        if (AnnotationUtils.isAnnotationPresent(targetMethod, RolePolicy.class)) {
            return new RolePredicate(AnnotationUtils.getAnnotation(targetMethod, RolePolicy.class).orElse(null));
        }
        return new RolePredicate(null);
    }

    @Override
    public String toString() {
        return "RolePredicate{" +
                "policy=" + policy +
                '}';
    }
}
