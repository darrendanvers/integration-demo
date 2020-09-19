package dev.codestijl.integrationdemo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functions handy when running the tests.
 *
 * @author darren
 * @since 1.0.0
 */
public final class TestUtils {

    private static final Pattern GUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$");

    /**
     * Checks a String to see if it is a valid GUID based. This does not validate global uniqueness, just that
     * it has the correct pattern.
     *
     * @param idToTest The String to check to see if it is a GUID.
     * @return True if idToTest is a valid GUID and false otherwise.
     */
    public static boolean isGuid(final String idToTest) {

        final Matcher guidMatcher = GUID_PATTERN.matcher(idToTest);
        return guidMatcher.matches();
    }

    private TestUtils() {
        // Intentionally empty.
    }
}
