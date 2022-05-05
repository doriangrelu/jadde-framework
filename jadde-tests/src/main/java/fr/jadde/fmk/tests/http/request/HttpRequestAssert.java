package fr.jadde.fmk.tests.http.request;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import java.util.List;

public class HttpRequestAssert {

    private final WebClient client;
    private final VertxTestContext testContext;
    private final Future<String> httpHost;
    private final Future<Integer> httpPort;


    private final HttpMethod method;
    private final String path;
    private final Checkpoint checkpoint;
    private boolean alreadySend = false;
    private HttpRequest<Buffer> request;


    private Future<Void> serverFuture;

    public HttpRequestAssert(WebClient client, VertxTestContext testContext, Future<String> httpHost, Future<Integer> httpPort, HttpMethod method, String path, Checkpoint checkpoint) {
        this.client = client;
        this.testContext = testContext;
        this.httpHost = httpHost;
        this.httpPort = httpPort;
        this.method = method;
        this.path = path;
        this.checkpoint = checkpoint;

        final Promise<Void> serverPromise = Promise.promise();
        this.serverFuture = serverPromise.future();
        final List<Future> server = List.of(this.httpHost, this.httpPort);
        CompositeFuture.all(server).onSuccess(dummy -> {
            this.request = client.request(method, (Integer) server.get(1).result(), (String) server.get(0).result(), this.path);
            serverPromise.complete();
        }).onFailure(serverPromise::fail);
    }

    public HttpRequestAssert withAuthenticationToken(final String token) {
        this.serverFuture = this.serverFuture.compose(unused -> {
            this.request.putHeader("Authorization", "Bearer " + token);
            return Future.succeededFuture();
        });
        return this;
    }

    @SuppressWarnings("rawtypes")
    public Future<HttpResponse<Buffer>> send() {
        this.checkStatusOrFail();
        this.alreadySend = true;
        final Promise<HttpResponse<Buffer>> bufferPromise = Promise.promise();
        this.serverFuture.onSuccess(dummy -> {
            request.send()
                    .onComplete(testContext.succeeding(response ->
                            testContext.verify(() -> {
                                bufferPromise.complete(response);
                                this.checkpoint.flag();
                            })
                    ));
        });
        return bufferPromise.future();
    }

    @SuppressWarnings("rawtypes")
    public Future<HttpResponse<Buffer>> send(final Object body) {
        this.checkStatusOrFail();
        this.alreadySend = true;
        final List<Future> server = List.of(this.httpHost, this.httpPort);
        final Promise<HttpResponse<Buffer>> bufferPromise = Promise.promise();
        CompositeFuture.all(server).onSuccess(dummy -> {
            client.request(method, (Integer) server.get(1).result(), (String) server.get(0).result(), this.path)
                    .sendJson(body)
                    .onComplete(testContext.succeeding(response ->
                            testContext.verify(() -> {
                                bufferPromise.complete(response);
                                this.checkpoint.flag();
                            })
                    ));
        });
        return bufferPromise.future();
    }

    private void checkStatusOrFail() {
        if (this.alreadySend) {
            this.testContext.failNow("Cannot send request because already send");
        }
    }

}
