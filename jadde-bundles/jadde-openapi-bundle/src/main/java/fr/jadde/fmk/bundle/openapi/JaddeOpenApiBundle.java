package fr.jadde.fmk.bundle.openapi;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;
import fr.jadde.fmk.bundle.openapi.api.OpenApiSpecificationProcessor;
import fr.jadde.fmk.bundle.openapi.service.OpenApiAdapterResolverInvoker;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

public class JaddeOpenApiBundle extends AbstractJaddeBundle {
    @Override
    public boolean next(JaddeApplicationContext context) {
//        todo WIP
//        OpenAPI openAPI = new OpenAPIV3Parser().read("C:\\DEV\\repositories\\jadde-framework\\jadde-bundles\\jadde-openapi-bundle\\src\\main\\resources\\my_test.yaml"); // for OpenAPI3.0
//        final var u = context.container().unsafeResolve(OpenApiAdapterResolverInvoker.class);
//        u.startProcess(openAPI);
        return this.handleNext(context);
    }

    @Override
    public String getName() {
        return "toto";
    }

}
