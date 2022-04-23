package fr.jadde.fmk.app.context.configuration;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.ServerSocket;

public class Configuration {

    private final ConfigRetriever retriever;

    public Configuration(final ConfigRetriever retriever) {
        this.retriever = retriever;
    }

    public Future<JsonObject> all() {
        final Promise<JsonObject> promise = Promise.promise();
        retriever.getConfig(json -> {
            if (json.succeeded()) {
                promise.complete(json.result());
            } else {
                promise.fail(json.cause());
            }
        });
        return promise.future();
    }

    public Future<Integer> freeServerPort() {
        return this.all().map(entries -> {
            Integer port = entries.getInteger("port");
            if (null == port || port == 0) {
                try {
                    final ServerSocket socket = new ServerSocket(0);
                    port = socket.getLocalPort();
                    socket.close();
                } catch (IOException e) {
                    port = 8080;
                }
            }
            return port;
        });
    }

}
