package fr.jadde.fmk.tests;

import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.tests.http.TestHttpClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public abstract class AbstractJaddeTest {

    private static final Logger logger = LoggerFactory.getLogger("TestLogs");

    protected VertxTestContext vertxTestContext;

    protected void setVertxTestContext(final VertxTestContext vertxTestContext) {
        this.vertxTestContext = vertxTestContext;
    }

    protected TestHttpClient httpClient(final JaddeApplicationContext context) {
        if (null == this.vertxTestContext) {
            throw new IllegalStateException("Missing VertX test context, please use setVertxTestContext(context)");
        }
        logger.info("Create HTTP client");
        return new TestHttpClient(context, vertxTestContext);
    }

    protected void log(final String message, final Object ...args) {
        logger.info("### " + message, args);
    }

}
