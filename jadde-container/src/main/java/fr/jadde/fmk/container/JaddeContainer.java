package fr.jadde.fmk.container;

import dagger.ObjectGraph;
import fr.jadde.fmk.container.annotation.JaddeModule;
import fr.jadde.fmk.container.tools.ClassConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JaddeContainer {

    private final Map<Class<?>, Object> modules;

    private final Map<Class<?>, Object> managedObject;

    private JaddeContainer() {
        this.modules = new ConcurrentHashMap<>();
        this.managedObject = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public JaddeContainer withModule(Class<?> moduleClassName) {
        if (null != moduleClassName.getAnnotation(JaddeModule.class)) {
            final Optional<Object> instance = ClassConstructor.<Object>createInstance((Class<Object>) moduleClassName);
            instance.ifPresentOrElse(
                    module -> this.modules.put(moduleClassName, module),
                    () -> {
                        //todo log
                    }
            );
        } else {
            // todo log
        }

        return this;
    }


    public void start() {
        final ObjectGraph graph = ObjectGraph.create(this.modules.values());
    }


    public static JaddeContainer create() {
        return new JaddeContainer();
    }

}
