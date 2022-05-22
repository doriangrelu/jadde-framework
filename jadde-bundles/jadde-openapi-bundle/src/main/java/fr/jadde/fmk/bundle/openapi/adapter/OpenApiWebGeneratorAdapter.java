package fr.jadde.fmk.bundle.openapi.adapter;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.bundle.openapi.api.OpenApiSpecificationProcessor;
import fr.jadde.fmk.container.annotation.JaddeBean;
import fr.jadde.fmk.container.annotation.Qualifier;
import io.swagger.v3.oas.models.OpenAPI;

@JaddeBean
@Qualifier("openapi.web.generator.adapter")
public class OpenApiWebGeneratorAdapter implements OpenApiSpecificationProcessor {

    @Inject
    private JaddeApplicationContext jaddeApplicationContext;

    @Override
    public void start(OpenAPI openAPI) {
        if (!openAPI.getPaths().isEmpty()) {
            openAPI.getPaths();
        }
    }

}
