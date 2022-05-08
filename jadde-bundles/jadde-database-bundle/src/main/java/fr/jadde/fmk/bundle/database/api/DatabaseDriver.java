package fr.jadde.fmk.bundle.database.api;

import com.speedment.jpastreamer.application.JPAStreamer;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

public interface DatabaseDriver {

    JPAStreamer streamer();

    JPAStreamer streamer(final String name);

    EntityManager manager();

    EntityManager manager(final String name);

    <T> Stream<T> stream(final Class<T> target);

    <T> Stream<T> stream(final String name, final Class<T> target);

}
