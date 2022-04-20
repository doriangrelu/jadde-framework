package fr.jadde.fmk.app.reflection;

import com.google.common.reflect.ClassPath;
import fr.jadde.fmk.app.exception.PackageScanningException;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class JaddePackageScanner {

    public static Set<Class<?>> scan(final Class<?> rootApplicationClassName) throws PackageScanningException {
        try {
            return findAllClassesUsingClassLoader(rootApplicationClassName.getPackageName());
        } catch (IOException e) {
            throw new PackageScanningException("Error during package scanning", e);
        }
    }

    public static Set<Class<?>> findAllClassesUsingClassLoader(String packageName) throws IOException {
        return ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName()
                        .startsWith(packageName))
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toSet());
    }


}
