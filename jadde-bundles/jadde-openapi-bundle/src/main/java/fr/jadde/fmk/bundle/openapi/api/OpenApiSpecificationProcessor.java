package fr.jadde.fmk.bundle.openapi.api;

import io.swagger.v3.oas.models.OpenAPI;

public interface OpenApiSpecificationProcessor {

    void start(final OpenAPI openAPI);

}
