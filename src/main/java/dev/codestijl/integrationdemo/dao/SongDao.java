package dev.codestijl.integrationdemo.dao;

import dev.codestijl.integrationdemo.entity.Song;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * DAO for working wth the SONG table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class SongDao implements Dao<Song> {

    private static final String INSERT_SQL = "INSERT INTO STAGE.SONG " +
            "(SONG_ID, ALBUM_ID, CREATE_TIME, SONG_NAME) " +
            "VALUES (?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    /**
     * BatchPreparedStatementSetter to insert rows in the SONG table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class SongInsert extends BaseBatchPreparedStatementSetter<Song> {

        private SongInsert(final Collection<? extends Song> data) {
            super(data);
        }

        @Override
        protected void doSetValues(final PreparedStatement preparedStatement, final Song value) throws SQLException {

            preparedStatement.setString(1, value.getSongId());
            preparedStatement.setString(2, value.getAlbumId());
            preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            preparedStatement.setString(4, value.getSongName());
        }
    }

    /**
     * Constructs a new SongDao.
     *
     * @param jdbcTemplate The JdbcTemplate to use to run queries.
     */
    public SongDao(final JdbcTemplate jdbcTemplate) {

        Assert.notNull(jdbcTemplate, "JdbcTemplate cannot be null.");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insert(final Collection<? extends Song> toInsert) {

        final int[] rowsInserted = this.jdbcTemplate.batchUpdate(INSERT_SQL, new SongDao.SongInsert(toInsert));

        return Arrays.stream(rowsInserted).sum();
    }
}
