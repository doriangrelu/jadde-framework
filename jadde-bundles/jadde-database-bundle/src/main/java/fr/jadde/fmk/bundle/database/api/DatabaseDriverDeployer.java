package fr.jadde.fmk.bundle.database.api;

import java.util.HashMap;
import java.util.Map;

public interface DatabaseDriverDeployer {

    default DatabaseDriverDeployer deploy(final String unitIdentifier) {
        return this.deploy(unitIdentifier, new HashMap<>());
    }

    DatabaseDriverDeployer deploy(final String unitIdentifier, final Map<String, String> properties);

}
