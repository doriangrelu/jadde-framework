package fr.jadde.fmk.tests.http;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.tests.http.request.HttpRequestAssert;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

public class TestHttpClient {

    private final JaddeApplicationContext jaddeApplicationContext;

    private final VertxTestContext vertxTestContext;

    private final WebClient client;

    public TestHttpClient(final JaddeApplicationContext jaddeContext, final VertxTestContext vertxTestContext) {
        this.jaddeApplicationContext = jaddeContext;
        this.vertxTestContext = vertxTestContext;
        this.client = WebClient.create(jaddeContext.vertx());
    }

    public HttpRequestAssert request(final HttpMethod method, final String path) {
        final Checkpoint haveConfiguration = this.vertxTestContext.checkpoint();
        final Checkpoint request = this.vertxTestContext.checkpoint();
        final Promise<Integer> httpPort = Promise.promise();
        final Promise<String> httpHost = Promise.promise();
        this.jaddeApplicationContext.<JsonObject>waitForParameter("server.configuration")
                .onComplete(server -> {
                    httpPort.complete(server.result().getInteger("port"));
                    httpHost.complete(server.result().getString("host"));
                    haveConfiguration.flag();
                });
        return new HttpRequestAssert(this.client, this.vertxTestContext, httpHost.future(), httpPort.future(), method, path, request);
    }

}
