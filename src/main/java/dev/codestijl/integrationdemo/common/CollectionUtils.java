package dev.codestijl.integrationdemo.common;

import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

/**
 * A collection of utility functions related to Collections.
 *
 * @author darren
 * @since 1.0.0
 */
public final class CollectionUtils {

    private static final String AS_STRING_FORMAT = "{%s}";

    /**
     * Takes a Collection and returns a String representation of it. The string will be built by calling toString
     * on each element of the collection. Each element is separate by commas. The entire string is bound by curly
     * braces.
     *
     * @param collection The Collection to return a String representation of.
     * @param <T> The type of object in the Collection.
     * @return A String representation of the Collection.
     */
    public static <T> String asString(final Collection<T> collection) {

        return String.format(AS_STRING_FORMAT, collectionToString(collection));
    }

    /**
     * Takes a Collection and returns a String representation of it. This method is similar to the one without
     * the maxLength parameter in the format it produces. The difference is the generated string will be at most
     * maxLength characters. The enclosing curly braces will always be present (these are included in count for the
     * max length of the string). The Sting inside the braces will be trimmed to fit inside the max length. If the
     * String is trimmed, it will end with eclipses.
     *
     * @param collection The Collection to return a String representation of.
     * @param maxLength The maximum length of the generated String.
     * @param <T> The type of object in the Collection.
     * @return A String representation of the Collection.
     */
    public static <T> String asString(final Collection<T> collection, final int maxLength) {

        final int maxWithoutBraces = maxLength - 2;
        final String abbreviatedString = StringUtils.abbreviate(collectionToString(collection), maxWithoutBraces);

        return String.format(AS_STRING_FORMAT, abbreviatedString);
    }

    private static <T> String collectionToString(final Collection<T> collection) {

        if (Objects.isNull(collection)) {
            return StringUtils.EMPTY;
        }

        final StringJoiner stringJoiner = new StringJoiner(",");
        collection.forEach(c -> stringJoiner.add(c.toString()));
        return stringJoiner.toString();
    }

    private CollectionUtils() {
        // Intentionally empty.
    }
}
