package fr.jadde.fmk.bundle.security;

import fr.jadde.fmk.app.context.configuration.Parameter;
import fr.jadde.fmk.app.executor.bean.api.AbstractJaddeBeanProcessor;
import fr.jadde.fmk.bundle.security.annotation.AnyRole;
import fr.jadde.fmk.bundle.security.annotation.Authentication;
import fr.jadde.fmk.bundle.security.annotation.HaveRoles;
import fr.jadde.fmk.bundle.web.tools.PathUtils;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import java.util.Set;
import java.util.stream.Stream;

public class JaddeSecurityProcessor extends AbstractJaddeBeanProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JaddeSecurityProcessor.class);

    @Override
    public void process(Object target) {
        final String rootPath = target.getClass().isAnnotationPresent(Path.class) ? target.getClass().getAnnotation(Path.class).value() : null;
        if (null != rootPath && (target.getClass().isAnnotationPresent(AnyRole.class) || target.getClass().isAnnotationPresent(HaveRoles.class))) {
            logger.info("Use global security policy for '{}'", target.getClass());
            if (target.getClass().isAnnotationPresent(AnyRole.class)) {
                this.addIsAuthenticatedPolicyPolicy(rootPath, false);
            } else {
                this.addIsAuthenticatedPolicyPolicy(rootPath, Set.of(target.getClass().getAnnotation(HaveRoles.class).value()), false);
            }
        } else {
            logger.warn("Use only local method policy for '{}'", target.getClass());
        }

        Stream.of(target.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Path.class))
                .filter(method -> method.isAnnotationPresent(AnyRole.class) || method.isAnnotationPresent(HaveRoles.class))
                .forEach(method -> {
                    final String localPath = method.getAnnotation(Path.class).value();
                    if (method.isAnnotationPresent(AnyRole.class)) {
                        this.addIsAuthenticatedPolicyPolicy(PathUtils.composePath(rootPath, localPath), true);
                    } else {
                        this.addIsAuthenticatedPolicyPolicy(
                                PathUtils.composePath(rootPath, localPath),
                                Set.of(method.getAnnotation(HaveRoles.class).value()),
                                true
                        );
                    }
                });
    }

    private void addIsAuthenticatedPolicyPolicy(final String path, final boolean doesExact) {
        this.context().<Router>waitForParameter(Parameter.ROUTER.parameterName())
                .onSuccess(router -> {
                    final String pathPattern = this.makePathPattern(PathUtils.composePath(path), doesExact);
                    final OAuth2AuthHandler handler = OAuth2AuthHandler.create(this.context().vertx(), this.context().parameter(Parameter.AUTH_PROVIDER.parameterName()));
                    router.route(pathPattern).handler(handler);
                    logger.info("Successfully applied security policy --> ANY_ROLE[{}]", pathPattern);
                })
                .onFailure(throwable -> {
                    throw new IllegalStateException("Cannot set security policy", throwable);
                });
    }

    /**
     * todo refactor content with method --> addIsAuthenticatedPolicyPolicy
     *
     * @param path
     * @param roles
     * @param doesExact
     */
    private void addIsAuthenticatedPolicyPolicy(final String path, final Set<String> roles, final boolean doesExact) {
        this.context().<Router>waitForParameter(Parameter.ROUTER.parameterName())
                .onSuccess(router -> {
                    final String pathPattern = this.makePathPattern(PathUtils.composePath(path), doesExact);
                    final OAuth2AuthHandler handler = OAuth2AuthHandler.create(this.context().vertx(), this.context().parameter(Parameter.AUTH_PROVIDER.parameterName()));
                    router.route(pathPattern)
                            .handler(handler)
                    // todo make handler roles
                    ;
                    logger.info("Successfully applied security policy --> {}[{}]", roles.toArray(), pathPattern);
                })
                .onFailure(throwable -> {
                    throw new IllegalStateException("Cannot set security policy", throwable);
                });
    }

    private String makePathPattern(final String path, final boolean doesExact) {
        return doesExact ? path : path + "/*";
    }

    @Override
    public boolean doesSupport(Object target) {
        return target != null && target.getClass().isAnnotationPresent(Authentication.class);
    }
}
