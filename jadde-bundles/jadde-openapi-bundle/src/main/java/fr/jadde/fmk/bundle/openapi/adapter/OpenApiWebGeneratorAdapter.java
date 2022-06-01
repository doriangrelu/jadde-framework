package fr.jadde.fmk.bundle.openapi.adapter;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.bundle.openapi.api.CodeGeneratorEngine;
import fr.jadde.fmk.bundle.openapi.api.OpenApiSpecificationProcessor;
import fr.jadde.fmk.container.annotation.JaddeBean;
import fr.jadde.fmk.container.annotation.Qualifier;
import io.swagger.v3.oas.models.OpenAPI;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@JaddeBean
@Qualifier("openapi.web.generator.adapter")
public class OpenApiWebGeneratorAdapter implements OpenApiSpecificationProcessor {

    @Inject
    private JaddeApplicationContext jaddeApplicationContext;

    @Inject
    private CodeGeneratorEngine engine;


    @Override
    public void start(OpenAPI openAPI) {
       /* if (!openAPI.getPaths().isEmpty()) {
            openAPI.getPaths();
        } */
        try {
            final Map<String, Object> context = new HashMap<>();
            context.put("package", "fr.jadde.toto");
            context.put("className", "Dodo");

            final String code = new String(this.engine.generate("templates/_base_controller.pebble", context));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
