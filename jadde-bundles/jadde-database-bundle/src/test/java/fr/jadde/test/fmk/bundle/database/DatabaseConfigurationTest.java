package fr.jadde.test.fmk.bundle.database;

import com.speedment.jpastreamer.application.JPAStreamer;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConfigurationTest {

    @Test
    void test() {
        final Map<String, String> properties = new HashMap<>();

        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.connection.url", "jdbc:h2:mem:test");
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");

        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("UsersDB", properties);

        var streamer = JPAStreamer.createJPAStreamerBuilder(emf);




    }

}
