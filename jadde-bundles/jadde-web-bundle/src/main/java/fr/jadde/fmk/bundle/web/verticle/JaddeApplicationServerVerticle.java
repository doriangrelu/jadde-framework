package fr.jadde.fmk.bundle.web.verticle;

import fr.jadde.fmk.app.context.configuration.Parameter;
import fr.jadde.fmk.app.verticle.AbstractJaddeVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class JaddeApplicationServerVerticle extends AbstractJaddeVerticle {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationServerVerticle.class);


    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        final HttpServer server = vertx.createHttpServer().requestHandler(router);

        this.context().configuration().serverConfiguration().onSuccess(serverConfiguration -> {
            server.listen(serverConfiguration.getInteger("port"), serverConfiguration.getString("host"), res -> {
                if (res.succeeded()) {
                    logger.info("Successfully starter HTTP Web server  '" + serverConfiguration.getString("host") + ':' + serverConfiguration.getInteger("port") + "'");
                    this.context().withParameter("server.configuration", serverConfiguration);
                    startPromise.complete();
                } else {
                    logger.info("Cannot start HTTP Web server on local port '" + serverConfiguration.getInteger("port") + "'", res.cause());
                    startPromise.fail(res.cause());
                }
            });
            this.context()
                    .withParameter(Parameter.SERVER.parameterName(), server)
                    .withParameter(Parameter.ROUTER.parameterName(), router)
                    .withParameter(Parameter.PORT.parameterName(), serverConfiguration.getInteger("port"))
                    .withParameter(Parameter.HOST.parameterName(), serverConfiguration.getString("host"));

        }).onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        logger.info("Requested stop HTTP Web Jadde Verticle");
        this.context().<HttpServer>waitForParameter("server").compose(HttpServer::close)
                .onComplete(dummy -> {
                    if (dummy.succeeded()) {
                        logger.info("HTTP Web server stopped successfully");
                    }
                    stopPromise.tryComplete();
                });
    }
}
