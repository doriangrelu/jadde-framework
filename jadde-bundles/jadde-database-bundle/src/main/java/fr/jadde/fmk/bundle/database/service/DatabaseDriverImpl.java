package fr.jadde.fmk.bundle.database.service;

import com.speedment.jpastreamer.application.JPAStreamer;
import fr.jadde.fmk.bundle.database.api.DatabaseDriver;
import fr.jadde.fmk.bundle.database.api.DatabaseDriverDeployer;
import fr.jadde.fmk.container.exception.MissingEmptyConstructorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DatabaseDriverImpl implements DatabaseDriver, DatabaseDriverDeployer {

    public static final Logger logger = LoggerFactory.getLogger(DatabaseDriverImpl.class);

    private final Map<String, JPAStreamer> streamers;
    private final Map<String, EntityManager> managers;


    public DatabaseDriverImpl() {
        this.streamers = new ConcurrentHashMap<>();
        this.managers = new ConcurrentHashMap<>();
    }

    public JPAStreamer streamer() {
        return this.streamers.values().stream().findFirst().orElseThrow(() -> new MissingEmptyConstructorException("Default"));
    }

    public JPAStreamer streamer(final String name) {
        return Optional.ofNullable(this.streamers.getOrDefault(name, null)).orElseThrow(() -> new MissingEmptyConstructorException(name));
    }

    public EntityManager manager() {
        return this.managers.values().stream().findFirst().orElseThrow(() -> new MissingEmptyConstructorException("Default"));
    }

    public EntityManager manager(final String name) {
        return Optional.ofNullable(this.managers.getOrDefault(name, null)).orElseThrow(() -> new MissingEmptyConstructorException(name));
    }

    public <T> Stream<T> stream(final Class<T> target) {
        return this.streamer().stream(target);
    }

    public <T> Stream<T> stream(final String name, final Class<T> target) {
        return this.streamer(name).stream(target);
    }

    @Override
    public DatabaseDriverDeployer deploy(final String unitIdentifier, final Map<String, String> properties) {
        this.streamers.computeIfAbsent(unitIdentifier, id -> {
            logger.info("Deploy {} persistence unit", unitIdentifier);
            final EntityManagerFactory emf = Persistence.createEntityManagerFactory(id, properties);
            this.managers.put(unitIdentifier, emf.createEntityManager());
            return JPAStreamer.of(emf);
        });
        return this;
    }


}
