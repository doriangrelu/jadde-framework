package fr.jadde.fmk.bundle.openapi.service;


import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.bundle.openapi.api.OpenApiSpecificationProcessor;
import fr.jadde.fmk.container.annotation.JaddeBean;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

@JaddeBean
public class OpenApiAdapterResolverInvoker {

    @Inject
    private List<OpenApiSpecificationProcessor> adapters;

    /**
     * todo
     *
     * @param openAPI
     */
    public void startProcess(final OpenAPI openAPI) {

    }

}
