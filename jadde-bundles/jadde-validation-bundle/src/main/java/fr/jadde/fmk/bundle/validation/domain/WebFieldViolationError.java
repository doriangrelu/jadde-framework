package fr.jadde.fmk.bundle.validation.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WebFieldViolationError {

    @JsonProperty("field")
    private final String field;

    @JsonProperty("message")
    private final String message;

    @JsonCreator
    public WebFieldViolationError(@JsonProperty("field") final String field, @JsonProperty("message") final String message) {
        this.field = field;
        this.message = message;
    }

    public String field() {
        return this.field;
    }

    public String message() {
        return this.message;
    }
}
