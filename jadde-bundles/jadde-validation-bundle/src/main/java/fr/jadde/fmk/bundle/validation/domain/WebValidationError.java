package fr.jadde.fmk.bundle.validation.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public class WebValidationError {

    @JsonProperty("status")
    private final int status;

    @JsonProperty("errors")
    private final List<WebFieldViolationError> errors;

    @JsonCreator
    public WebValidationError(@JsonProperty("status") final int status, @JsonProperty("errors") final List<WebFieldViolationError> errors) {
        this.status = status;
        this.errors = errors;
    }

    public int status() {
        return this.status;
    }

    public List<WebFieldViolationError> errors() {
        return Collections.unmodifiableList(this.errors);
    }
}
