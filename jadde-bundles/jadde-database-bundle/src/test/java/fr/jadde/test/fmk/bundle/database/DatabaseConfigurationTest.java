package fr.jadde.test.fmk.bundle.database;

import fr.jadde.fmk.app.JaddeApplication;
import fr.jadde.fmk.app.context.JaddeApplicationContext;
import fr.jadde.fmk.bundle.database.api.DatabaseDriver;
import fr.jadde.fmk.tests.AbstractJaddeTest;
import fr.jadde.test.fmk.bundle.database.entity.Person;
import fr.jadde.test.fmk.bundle.database.entity.Person$;
import fr.jadde.test.fmk.bundle.database.mock.FakeDatabaseApplication;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.FlushModeType;

public class DatabaseConfigurationTest extends AbstractJaddeTest {

    @Test
    void shouldInitializeDatabaseContext(final Vertx vertx, final VertxTestContext testContext) {
        final JaddeApplicationContext context = JaddeApplication.start(FakeDatabaseApplication.class, new String[0], vertx);
        Assertions.assertThat(context.container().resolve(DatabaseDriver.class)).isNotEmpty();
        testContext.completeNow();
    }

    @Test
    void shouldWorkFine(final Vertx vertx, final VertxTestContext testContext) {
        final JaddeApplicationContext context = JaddeApplication.start(FakeDatabaseApplication.class, new String[0], vertx);
        final DatabaseDriver driver = context.container().resolve(DatabaseDriver.class).orElseThrow();

        driver.manager().setFlushMode(FlushModeType.AUTO);

        this.createPerson(driver, "Dorian", "GRELU");
        this.createPerson(driver, "Jean", "Michel");
        this.createPerson(driver, "Jeanne", "Michel");

        Assertions.assertThat(driver.stream(Person.class).toList().size()).isEqualTo(3);
        Assertions.assertThat(driver.stream(Person.class).filter(Person$.lastname.equal("Michel")).toList().size()).isEqualTo(2);
        Assertions.assertThat(driver.stream(Person.class).filter(Person$.firstname.contains("Jean")).toList().size()).isEqualTo(2);

        testContext.completeNow();
    }

    private void createPerson(final DatabaseDriver driver, final String firstname, final String lastname) {
        driver.manager().getTransaction().begin();
        final Person person = new Person();
        person.setFirstname(firstname);
        person.setLastname(lastname);
        driver.manager().persist(person);
        driver.manager().getTransaction().commit();

    }

}
