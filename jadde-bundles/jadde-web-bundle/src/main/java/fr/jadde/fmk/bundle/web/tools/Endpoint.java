package fr.jadde.fmk.bundle.web.tools;

public class Endpoint {

    private final String path;

    private final String method;

    private final String consumes;

    private final String produces;


    public Endpoint(String path, String method, String consumes, String produces) {
        this.path = path;
        this.method = method;
        this.consumes = consumes;
        this.produces = produces;
    }

    public String path() {
        return path;
    }

    public String method() {
        return method;
    }

    public String consumes() {
        return consumes;
    }

    public String produces() {
        return produces;
    }
}
