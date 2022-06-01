package fr.jadde.fmk.bundle.openapi.service;

import com.mitchellbosecke.pebble.PebbleEngine;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bean.annotation.Inject;
import fr.jadde.fmk.bundle.openapi.api.CodeGeneratorEngine;
import fr.jadde.fmk.container.annotation.Default;
import fr.jadde.fmk.container.annotation.JaddeBean;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@JaddeBean
@Default
@Slf4j
public class CodeGeneratorEngineImpl implements CodeGeneratorEngine {

    @Inject
    private JaddeApplicationContext context;

    private final PebbleEngine pebbleEngine;

    public CodeGeneratorEngineImpl() {
        this.pebbleEngine = new PebbleEngine.Builder().build();
    }

    @Override
    public byte[] generate(String templatePath, Map<String, Object> context) throws IOException, URISyntaxException {
        final URL classPathURL = CodeGeneratorEngineImpl.class.getClassLoader()
                .getResource(templatePath);
        final Path template = Paths.get(Objects.requireNonNull(classPathURL).toURI());
        final byte[] fileContent = Files.readAllBytes(template);
        final AtomicReference<String> strFileContent = new AtomicReference<>(new String(fileContent));

        context.forEach((key, value) -> {
            strFileContent.set(strFileContent.get().replaceAll("\\{\\{" + key + "\\}\\}", value.toString()));
        });


        return fileContent;
    }

    private String evaluateIterator(final String strContentent) {
        
    }


}
