package fr.jadde.fmk.bundle.dispatcher.api.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Event<T> {

    @JsonProperty
    private final Headers headers;

    @JsonProperty
    private final T body;

    @JsonCreator
    public Event(@JsonProperty("headers") Headers headers, @JsonProperty("body") T body) {
        this.headers = headers;
        this.body = body;
    }


    public Headers headers() {
        return headers;
    }

    public T body() {
        return body;
    }
}
