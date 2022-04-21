package fr.jadde.test.fmk.bundle.web;

import fr.jadde.test.fmk.bundle.web.mock.FakeApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JaddeWebAnnotationProcessorTest {

    @BeforeAll
    public static void setUp() {
        FakeApplication.start(FakeApplication.class, new String[0]);
    }

    @Test
    void shouldProcessAWebService() {

    }

}
