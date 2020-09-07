package dev.codestijl.integrationdemo.dao;

import dev.codestijl.integrationdemo.entity.AlbumError;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * DAO for working wth the ALBUM_ERROR table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumErrorDao implements Dao<AlbumError> {

    private static final String INSERT_SQL = "INSERT INTO STAGE.ALBUM_ERROR " +
            "(ERROR_ID, ALBUM_ID, BATCH_ID, CREATE_TIME, ERROR_TEXT) " +
            "VALUES (?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    /**
     * BatchPreparedStatementSetter to insert rows in the ALBUM_ERROR table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class ErrorInsert extends BaseBatchPreparedStatementSetter<AlbumError> {

        private ErrorInsert(final Collection<? extends AlbumError> messages) {
            super(messages);
        }

        @Override
        protected void doSetValues(final PreparedStatement preparedStatement, final AlbumError record) throws SQLException {

            preparedStatement.setString(1, record.getErrorId());
            preparedStatement.setString(2, record.getAlbumId());
            preparedStatement.setString(3, record.getBatchId());
            preparedStatement.setTimestamp(4, Timestamp.from(Instant.now()));
            preparedStatement.setString(5, record.getErrorText());
        }
    }

    /**
     * Constructs a new AlbumErrorDao.
     *
     * @param jdbcTemplate The JdbcTemplate to use to run queries.
     */
    public AlbumErrorDao(final JdbcTemplate jdbcTemplate) {

        Assert.notNull(jdbcTemplate, "JdbcTemplate cannot be null.");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insert(final Collection<? extends AlbumError> toInsert) {

        final int[] rowsInserted = this.jdbcTemplate.batchUpdate(INSERT_SQL, new ErrorInsert(toInsert));
        return Arrays.stream(rowsInserted).sum();
    }
}
