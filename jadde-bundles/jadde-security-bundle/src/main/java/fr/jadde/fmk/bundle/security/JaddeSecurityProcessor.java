package fr.jadde.fmk.bundle.security;

import fr.jadde.fmk.app.context.configuration.Parameter;
import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.app.utils.AnnotationUtils;
import fr.jadde.fmk.bundle.security.annotation.authentication.Authentication;
import fr.jadde.fmk.bundle.security.annotation.authorization.RolePolicy;
import fr.jadde.fmk.bundle.security.api.AuthorizationRoleIntrospect;
import fr.jadde.fmk.bundle.security.role.RolePredicate;
import fr.jadde.fmk.bundle.web.tools.PathUtils;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Jadde Security processor
 * This processor handles security annotation on controller's class
 * This processor wraps authentication handler and apply policy role authorization
 *
 * @author Dorian GRELU
 */
public class JaddeSecurityProcessor extends AbstractJaddeBeanProcessor {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(JaddeSecurityProcessor.class);

    /**
     * Safe state that contains lazy authentications' handler that created
     */
    private final Map<Class<? extends AuthenticationProvider>, AuthenticationHandler> lazyHandlers;

    /**
     * Constructor
     */
    public JaddeSecurityProcessor() {
        this.lazyHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Processes security wrap for target bean
     *
     * @param target target bean
     */
    @Override
    public void process(Object target) {
        // This is the global security policy
        final RolePredicate globalPredicate = RolePredicate.of(target.getClass());
        final String rootPath = target.getClass().isAnnotationPresent(Path.class) ? target.getClass().getAnnotation(Path.class).value() : null;

        // Introspects all public methods and apply locale policy
        Stream.of(target.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Path.class))
                .forEach(method -> {
                    final String localPath = method.getAnnotation(Path.class).value();
                    final RolePredicate localPredicate = RolePredicate.of(method).and(globalPredicate);
                    this.applySecurityPolicy(PathUtils.composePath(rootPath, localPath), localPredicate, true);
                });
    }

    /**
     * Applies security police for given path with predicate
     *
     * @param path      given path
     * @param predicate target predicate that use for authorization macth
     * @param doesExact exact match ?
     */
    private void applySecurityPolicy(final String path, final RolePredicate predicate, final boolean doesExact) {
        this.context().<Router>waitForParameter(Parameter.ROUTER.parameterName())
                .onSuccess(router -> {
                    final String pathPattern = this.makePathPattern(PathUtils.composePath(path), doesExact);
                    final Route targetRoute = router.route(pathPattern);
                    this.resisterOauth2AuthenticationHandler(targetRoute, this.context().parameter(Parameter.AUTH_PROVIDER.parameterName()));
                    targetRoute.handler(routingContext -> {
                        final Set<String> userRoles = this.resolveUserRoles(routingContext.user());
                        if (!predicate.doesAuthorize(userRoles)) {
                            routingContext.fail(new HttpException(401, "Missing required policy"));
                        } else {
                            routingContext.next();
                        }
                    });
                    logger.info("Successfully applied security policy --> {}[{}]", predicate.policyName(), pathPattern);
                })
                .onFailure(throwable -> {
                    throw new IllegalStateException("Cannot set security policy", throwable);
                });
    }

    /**
     * Triggers user role introspection
     *
     * @param user target user
     * @return set of roles
     */
    private Set<String> resolveUserRoles(final User user) {
        return this.context()
                .container()
                .resolveAll(AuthorizationRoleIntrospect.class).stream()
                .map(authorizationRoleIntrospect -> authorizationRoleIntrospect.introspect(user))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Registers OAuth2 handler for route
     *
     * @param targetRoute target route
     * @param providers   Jadde authentication provider that provided
     */
    private void resisterOauth2AuthenticationHandler(final Route targetRoute, final Set<AuthenticationProvider> providers) {
        providers.stream()
                .filter(authenticationProvider -> OAuth2Auth.class.isAssignableFrom(authenticationProvider.getClass()))
                .map(OAuth2Auth.class::cast)
                .map(authenticationProvider -> this.createHandler(authenticationProvider, () -> OAuth2AuthHandler.create(this.context().vertx(), authenticationProvider)))
                .forEach(targetRoute::handler);
    }

    /**
     * Creates target handler or return instance if already created
     *
     * @param provider target provider
     * @param creator  handler creator
     * @return target handler
     */
    private AuthenticationHandler createHandler(final AuthenticationProvider provider, final Supplier<AuthenticationHandler> creator) {
        return this.lazyHandlers.computeIfAbsent(provider.getClass(), providerClass -> creator.get());
    }

    /**
     * Creates URI pattern from path and exact match option
     *
     * @param path      target path
     * @param doesExact does match exactly
     * @return final path pattern
     */
    private String makePathPattern(final String path, final boolean doesExact) {
        return doesExact ? path : path + "/*";
    }

    /**
     * Supports target bean processing with follow conditions:
     * - Not null bean
     * - @Authentication || @AnyRole || @AllRoles || @AnyAuthenticated  annotation should be presents
     *
     * @param target the target bean which be processed
     * @return does support
     */
    @Override
    public boolean doesSupport(Object target) {
        return target != null && (target.getClass().isAnnotationPresent(Authentication.class) || AnnotationUtils.isAnnotationPresent(target.getClass(), RolePolicy.class));
    }

    @Override
    public int priorityOrder() {
        return 1;
    }


}
