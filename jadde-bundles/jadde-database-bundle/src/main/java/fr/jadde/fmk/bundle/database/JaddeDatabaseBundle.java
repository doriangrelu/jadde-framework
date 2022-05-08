package fr.jadde.fmk.bundle.database;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.app.executor.bundle.api.AbstractJaddeBundle;
import fr.jadde.fmk.bundle.database.api.DatabaseDriverDeployer;
import fr.jadde.fmk.bundle.database.service.DatabaseDriverImpl;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JaddeDatabaseBundle extends AbstractJaddeBundle {

    public static final String NAME = "jadde.database.bundle";

    private static final Logger logger = LoggerFactory.getLogger(JaddeDatabaseBundle.class);
    public static final String DEFAULT_JADDE_UNIT = "fr.jadde.fmk.bundle.database.Default";

    @Override
    public boolean next(JaddeApplicationContext context) {
        context.configuration().databaseConfiguration()
                .onSuccess(jsonObjects -> {
                    final DatabaseDriverDeployer deployer = context.container().registerAndGetInstance(DatabaseDriverImpl.class);
                    jsonObjects.forEach(entries -> {
                        final String name = entries.containsKey("name") ? entries.getString("name") : DEFAULT_JADDE_UNIT;
                        entries.remove("name");
                        deployer.deploy(name, this.formatJPAProperties(entries));
                    });
                }).onFailure(throwable -> logger.error("Cannot load database configuration", throwable));
        return this.handleNext(context);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> formatJPAProperties(final JsonObject config) {
        return config.mapTo(Map.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
