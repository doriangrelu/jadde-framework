package fr.jadde.fmk.app.context.configuration;

public enum Parameter {
    ROUTER("router", true),
    SERVER("server", true),
    PORT("port", false),
    HOST("host", false),
    AUTH_PROVIDER("auth_provider", false);

    private final String name;

    private final boolean isMandatory;

    Parameter(String name, boolean isMandatory) {
        this.name = name;
        this.isMandatory = isMandatory;
    }

    public String parameterName() {
        return this.name;
    }

    public boolean isMandatory() {
        return isMandatory;
    }
}
