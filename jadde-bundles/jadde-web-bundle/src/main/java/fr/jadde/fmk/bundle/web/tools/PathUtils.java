package fr.jadde.fmk.bundle.web.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PathUtils {

    public static final char PATH_SEPARATOR = '/';

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("^.*(\\{([a-zA-Z0-9]+)\\}).*$");

    public static String sanitizePath(final String path) {
        String clean = StringUtils.strip(path, String.valueOf(PATH_SEPARATOR)).trim();

        Matcher matcher = PARAMETER_PATTERN.matcher(clean);
        while (matcher.matches()) {
            clean = clean.replace(matcher.group(1), ":" + matcher.group(2));
            matcher = PARAMETER_PATTERN.matcher(clean);
        }
        return clean;
    }

    public static String composePath(final String... paths) {
        final String[] nonEmptyPaths = Stream.of(paths)
                .filter(s -> !s.isEmpty())
                .map(PathUtils::sanitizePath)
                .toArray(String[]::new);

        if (nonEmptyPaths.length == 0) {
            return String.valueOf(PATH_SEPARATOR);
        }

        return PATH_SEPARATOR + String.join(String.valueOf(PATH_SEPARATOR), nonEmptyPaths);
    }

}
