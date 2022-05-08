package fr.jadde.fmk.bundle.dispatcher.api.utils;

import fr.jadde.fmk.bundle.dispatcher.api.payload.Headers;
import io.vertx.core.MultiMap;

import java.util.HashMap;
import java.util.Map;

public class HeadersBuilder {

    private final Map<String, String> headers;

    private HeadersBuilder(final Map<String, String> headers) {
        this.headers = headers;
    }

    public HeadersBuilder with(final String key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    public HeadersBuilder with(final MultiMap vertxHeaders) {
        vertxHeaders.forEach((key, value) -> {
            this.with(key, value);
        });
        return this;
    }

    public Headers build() {
        return new Headers(this.headers);
    }

    public static HeadersBuilder create(final String key, final String value) {
        final Map<String, String> headers = new HashMap<>();
        headers.put(key, value);
        return new HeadersBuilder(headers);
    }

    public static HeadersBuilder create() {
        final Map<String, String> headers = new HashMap<>();
        return new HeadersBuilder(headers);
    }

}
