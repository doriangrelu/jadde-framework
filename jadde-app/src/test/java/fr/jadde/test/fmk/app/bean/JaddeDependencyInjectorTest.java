package fr.jadde.test.fmk.app.bean;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import fr.jadde.fmk.app.JaddeApplication;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.test.fmk.app.mock.services.MyFirstService;
import fr.jadde.test.fmk.app.mock.services.MySecondService;
import fr.jadde.test.fmk.app.mock.services.MyThirdService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class JaddeDependencyInjectorTest extends JaddeApplication {

    @Test
    void testShouldInjectSimpleDependencies() {
        Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.TRACE);

        final var context = JaddeApplication.start(JaddeDependencyInjectorTest.class, new String[0]);

        final var myService = context.container().resolve(MySecondService.class);
        final var myComplexService = context.container().resolve(MyFirstService.class);

        Assertions.assertThat(myService).isNotEmpty().containsInstanceOf(MySecondService.class);
        Assertions.assertThat(myComplexService).isNotEmpty().containsInstanceOf(MyFirstService.class);

        Assertions.assertThat(myService.orElseThrow().myFirstService()).isNotNull().isInstanceOf(MyFirstService.class);
        Assertions.assertThat(myService.orElseThrow().applicationContext()).isNotNull().isInstanceOf(JaddeApplicationContext.class);
        Assertions.assertThat(myService.orElseThrow().myFirstServices()).hasSize(1);

        Assertions.assertThat(myComplexService.orElseThrow().myService()).isInstanceOf(MyThirdService.class);
        Assertions.assertThat(myComplexService.orElseThrow().unresolvableService()).isNull();

    }


}
