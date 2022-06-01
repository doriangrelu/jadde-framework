package fr.jadde.fmk.bundle.openapi.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface CodeGeneratorEngine {

    byte[] generate(final String templatePath, final Map<String, Object> context) throws IOException, URISyntaxException;

}
