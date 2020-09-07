package dev.codestijl.integrationdemo.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/**
 * I don't like the behaviour of the Spring queryForObject method in JdbcTemplate. It will throw an error if no result
 * is found, which is rarely what I want. This object can be used with the query method of JdbcTemplate when an record
 * in the table may or may not exist. It has similar semantics to the queryForObject method in that it will throw an error
 * if more than one row is returned in the cursor, but, if there are zero rows in the cursor, it returns null instead of an error.
 *
 * @param <T> The type to map from the cursor.
 */
public class SingleResultReader<T> implements ResultSetExtractor<T> {

    private final RowMapper<T> rowMapper;

    /**
     * Creates a new SingleResultReader.
     *
     * @param rowMapper The RowMapper to use to map an object of type T from a ResultSet.
     */
    public SingleResultReader(final RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * If there is one record in the ResultSet, will return an object of type T. If there is not a record
     * in the ResultSet, will return null. If there is more than one record in the ResultSet,
     * throws an IncorrectResultSizeDataAccessException.
     *
     * @param resultSet The ResultSet to extract data from.
     * @return An object of type T read from the ResultSet or null.
     * @throws SQLException Any error interacting with the DB.
     * @throws DataAccessException An IncorrectResultSizeDataAccessException if there is more than one row in the ResultSet.
     * @throws RuntimeException Any error thrown by the RowMapper will be propagated.
     */
    @Override
    // I can't check if there are more records in the cursor until I've read the current, so
    // result has to be declared before the call to rs.next(). This is why the PrematureDeclaration and the DataflowAnomalyAnalysis
    // errors are ignored.
    // The DataAccessException is declared in the ResultSetExtractor interface itself.
    @SuppressWarnings({"PMD.PrematureDeclaration", "PMD.DataflowAnomalyAnalysis",  "PMD.AvoidUncheckedExceptionsInSignatures"})
    public T extractData(final ResultSet resultSet) throws SQLException, DataAccessException {

        if (!resultSet.next()) {
            return null;
        }

        final T result = this.rowMapper.mapRow(resultSet, 1);

        if (resultSet.next()) {
            throw new IncorrectResultSizeDataAccessException(1);
        }

        return result;
    }
}
