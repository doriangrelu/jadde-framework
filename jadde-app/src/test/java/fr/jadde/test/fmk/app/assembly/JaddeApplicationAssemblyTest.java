package fr.jadde.test.fmk.app.assembly;

import fr.jadde.test.fmk.app.mock.FakeApplication;
import fr.jadde.test.fmk.app.mock.services.MyFirstService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Dorian GRELU
 */
class JaddeApplicationAssemblyTest {

    @Test
    void testStartScanning() {
        FakeApplication.main(new String[]{});
        Assertions.assertThat(MyFirstService.containerIdentifier()).isNotEmpty();
    }

}
