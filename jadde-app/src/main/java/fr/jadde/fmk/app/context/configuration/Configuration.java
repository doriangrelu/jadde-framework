package fr.jadde.fmk.app.context.configuration;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Objects;

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
            final JsonObject server = entries.getJsonObject("server");
            int port;
            if (null == server || server.getInteger("port") == 0) {
                try {
                    final ServerSocket socket = new ServerSocket(0);
                    port = socket.getLocalPort();
                    socket.close();
                } catch (IOException e) {
                    port = 8080;
                }
            } else {
                port = server.getInteger("port");
            }
            return port;
        });
    }

    @SuppressWarnings("rawtypes")
    public Future<JsonObject> serverConfiguration() {
        final Promise<JsonObject> serverInformations = Promise.promise();
        final List<Future> server = List.of(this.freeServerPort(), this.host());
        CompositeFuture.all(server).onComplete(compositeFutureAsyncResult -> {
            serverInformations.complete(new JsonObject()
                    .put("port", server.get(0).result())
                    .put("host", server.get(1).result())
            );
        });
        return serverInformations.future();
    }

    public Future<JsonObject> authenticationConfiguration() {
        return this.all().map(entries -> {
            final JsonObject authentication = entries.getJsonObject("authentication");
            return Objects.requireNonNullElseGet(authentication, JsonObject::new);
        });
    }

    public Future<String> host() {
        return this.all().map(entries -> {
            final JsonObject server = entries.getJsonObject("server");
            if (null == server) {
                return "localhost";
            }
            return server.getString("host", "localhost");
        });
    }

}
