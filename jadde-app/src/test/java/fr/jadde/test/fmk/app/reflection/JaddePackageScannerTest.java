package fr.jadde.test.fmk.app.reflection;

import fr.jadde.fmk.app.exception.PackageScanningException;
import fr.jadde.fmk.app.reflection.JaddePackageScanner;
import fr.jadde.test.fmk.app.mock.FakeApplication;
import fr.jadde.test.fmk.app.mock.annotation.MyAnnot;
import fr.jadde.test.fmk.app.mock.services.MyFirstService;
import fr.jadde.test.fmk.app.mock.services.MySecondService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class JaddePackageScannerTest {

    @Test
    void testScanPackage() throws PackageScanningException {
        Assertions.assertThat(JaddePackageScanner.scan(FakeApplication.class)).hasSize(4)
                .contains(MySecondService.class, MyFirstService.class, FakeApplication.class, MyAnnot.class);
    }


}
