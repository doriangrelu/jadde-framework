package fr.jadde.fmk.bundle.dispatcher.api.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.eventbus.DeliveryOptions;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Headers implements Serializable {

    @JsonProperty
    private final Map<String, String> content;

    @JsonCreator
    public Headers(@JsonProperty("content") Map<String, String> content) {
        this.content = Collections.unmodifiableMap(content);
    }

    @SuppressWarnings("unchecked")
    public <T> T value(final String key) {
        return (T) this.content.get(key);
    }

    public <T> Optional<T> optionalValue(final String key) {
        return Optional.ofNullable(this.value(key));
    }

    public Headers inOptions(final DeliveryOptions options) {
        this.content.forEach(options::addHeader);
        return this;
    }

    @JsonProperty("content")
    public Map<String, Object> all() {
        return new HashMap<>(this.content);
    }

}
