package dev.codestijl.integrationdemo.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface for the classes that handle most database interaction.
 *
 * @param <T> The type this DAO handles.
 */
// This is a common industry term, so it is
// clear what this is.
@SuppressWarnings("PMD.ShortClassName")
public interface Dao<T> {

    /**
     * Inserts a collection of records into the DB.
     *
     * @param toInsert The collection of objects to insert.
     * @return The number of rows inserted.
     */
    int insert(Collection<? extends T> toInsert);

    /**
     * Convenience method when you only have one object to insert.
     *
     * @param toInsert The object to insert.
     * @return The number of rows inserted.
     */
    default int insert(T toInsert) {
        return insert(List.of(toInsert));
    }

    /**
     * Updates a collection of records in the DB. The default operation is a NOOP.
     *
     * @param toUpdate The collection of objects to update.
     * @return The number of rows updated.
     */
    default int update(Collection<? extends T> toUpdate) {
        return 0;
    }

    /**
     * Convenience method when you only have one object to update.
     *
     * @param toUpdate The object to update.
     * @return The number of rows updated.
     */
    default int update(T toUpdate) {
        return insert(List.of(toUpdate));
    }

    /**
     * Looks for a given entity by its ID.
     *
     * @param id The ID of the entity to look for.
     * @return The entity with that ID or empty if not found.
     */
    default Optional<T> findById(String id) {
        return Optional.empty();
    }

    /**
     * Utility method that takes a variable length list of Objects and returns that list as an array. This is
     * useful for the JdbcTemplate methods that take an Object array as a parameter.
     *
     * @param args The list of Objects to return as an array.
     * @return The arguments passed in as an array.
     */
    default Object[] argsAsArray(Object... args) {
        return args;
    }
}
