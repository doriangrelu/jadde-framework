package fr.jadde.fmk.bundle.database.api;

import java.util.HashMap;
import java.util.Map;

public interface DatabaseManagerDeployer {

    default DatabaseManagerDeployer deploy(final String unitIdentifier) {
        return this.deploy(unitIdentifier, new HashMap<>());
    }

    DatabaseManagerDeployer deploy(final String unitIdentifier, final Map<String, String> properties);

}
