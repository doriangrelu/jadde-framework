package fr.jadde.fmk.bundle.web.verticle;

import fr.jadde.fmk.app.verticle.AbstractJaddeVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class JaddeApplicationServerVerticle extends AbstractJaddeVerticle {

    private static final Logger logger = LoggerFactory.getLogger(JaddeApplicationServerVerticle.class);


    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        final HttpServer server = vertx.createHttpServer().requestHandler(router);

        this.context().configuration().freeServerPort().onSuccess(port -> {
            server.listen(port, res -> {
                if (res.succeeded()) {
                    logger.info("Successfully starter HTTP Web server on local port '" + port + "'");
                    startPromise.complete();
                } else {
                    logger.info("Cannot start HTTP Web server on local port '" + port + "'", res.cause());
                    startPromise.fail(res.cause());
                }
            });
            this.context()
                    .withParameter("server", server)
                    .withParameter("router", router)
                    .withParameter("port", port);

            router.route().handler(ctx -> {
                final HttpServerResponse response = ctx.response();
                response.putHeader("content-type", "text/plain");
                response.end("Welcome to Jadde application");
            });
        }).onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
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
