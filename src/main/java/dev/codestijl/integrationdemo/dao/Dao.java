package dev.codestijl.integrationdemo.dao;

import java.util.Collection;

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
     * Updates a collection of records in the DB. The default operation is a NOOP.
     *
     * @param toUpdate The collection of objects to update.
     * @return The number of rows updated.
     */
    default int update(Collection<? extends T> toUpdate) {
        return 0;
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
