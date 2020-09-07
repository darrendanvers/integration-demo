package dev.codestijl.integrationdemo.common;

import java.util.UUID;

/**
 * Utility to generate globally unique IDs.
 *
 * @author darren
 * @since 1.0.0
 */
public final class IdUtils {

    /**
     * Returns a new ID.
     *
     * @return A new ID.
     */
    public static String newId() {

        return UUID.randomUUID().toString();
    }

    private IdUtils() {
        // Intentionally empty.
    }
}
