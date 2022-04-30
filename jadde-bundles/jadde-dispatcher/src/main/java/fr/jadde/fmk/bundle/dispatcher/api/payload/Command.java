package fr.jadde.fmk.bundle.dispatcher.api.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.Promise;

public class Command<T> {

    @JsonProperty("headers")
    private final Headers headers;

    @JsonProperty("body")
    private final T body;

    @JsonIgnore
    private Promise<Object> promise;

    private Command(final Headers headers, final T body) {
        this.headers = headers;
        this.body = body;
    }

    public T body() {
        return body;
    }

    public void complete(final Object result) {
        this.promise.complete(result);
    }

    public void complete() {
        this.promise.complete();
    }

    public void fail(final String failure) {
        this.promise.fail(failure);
    }

    public void fail(final Throwable throwable) {
        this.promise.fail(throwable);
    }

    public Command<T> dispatch(final Promise<Object> promise) {
        if (null != this.promise) {
            throw new IllegalStateException("Cannot dispatch command, because already dispatcher");
        }
        this.promise = promise;
        return this;
    }

    @JsonCreator
    public static <B> Command<B> build(@JsonProperty("headers") final Headers headers, @JsonProperty("body") final B body) {
        return new Command<>(headers, body);
    }

}
