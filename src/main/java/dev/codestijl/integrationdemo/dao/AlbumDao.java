package dev.codestijl.integrationdemo.dao;

import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.Status;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;



/**
 * DAO for working wth the ALBUM table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumDao implements Dao<Album> {

    private static final String INSERT_SQL = "INSERT INTO STAGE.ALBUM " +
            "(ALBUM_ID, GTIN_14, ALBUM_NAME, ARTIST_NAME, BATCH_ID, CREATE_TIME, STATUS_CD, LAST_UPDATE_TIME) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = "UPDATE STAGE.ALBUM " +
            "SET STATUS_CD = ?, LAST_UPDATE_TIME = ? " +
            "WHERE ALBUM_ID = ?";

    /**
     * SQL to use to select rows from the ALBUM table.
     */
    public static final String SELECT_SQL = "SELECT ALBUM_ID, GTIN_14, ALBUM_NAME, ARTIST_NAME, STATUS_CD, BATCH_ID " +
            "FROM STAGE.ALBUM";

    /**
     * RowMapper that aligns with the SELECT_SQL defined for this DAO.
     */
    public static final RowMapper<Album> ROW_MAPPER = (rs, rowNum) ->
        new Album().setAlbumId(rs.getString("ALBUM_ID"))
                .setGtin14(rs.getString("GTIN_14"))
                .setAlbumName(rs.getString("ALBUM_NAME"))
                .setArtist(rs.getString("ARTIST_NAME"))
                .setBatchId(rs.getString("BATCH_ID"))
                .setStatus(Status.of(rs.getString("STATUS_CD")));

    private final JdbcTemplate jdbcTemplate;


    /**
     * BatchPreparedStatementSetter to insert rows in the ALBUM table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class AlbumInsert extends BaseBatchPreparedStatementSetter<Album> {

        private AlbumInsert(final Collection<? extends Album> data) {
            super(data);
        }

        @Override
        protected void doSetValues(final PreparedStatement preparedStatement, final Album value) throws SQLException {

            final Instant now = Instant.now();

            preparedStatement.setString(1, value.getAlbumId());
            preparedStatement.setString(2, value.getGtin14());
            preparedStatement.setString(3, value.getAlbumName());
            preparedStatement.setString(4, value.getArtist());
            preparedStatement.setString(5, value.getBatchId());
            preparedStatement.setTimestamp(6, Timestamp.from(now));
            preparedStatement.setString(7, value.getStatus().getId());
            preparedStatement.setTimestamp(8, Timestamp.from(now));
        }
    }

    /**
     * BatchPreparedStatementSetter to update rows in the ALBUM table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class AlbumUpdate extends BaseBatchPreparedStatementSetter<Album> {

        private AlbumUpdate(final Collection<? extends Album> data) {
            super(data);
        }

        @Override
        protected void doSetValues(final PreparedStatement preparedStatement, final Album value) throws SQLException {

            preparedStatement.setString(1, value.getStatus().getId());
            preparedStatement.setTimestamp(2, Timestamp.from(Instant.now()));
            preparedStatement.setString(3, value.getAlbumId());
        }
    }

    /**
     * Constructs an AlbumDao.
     *
     * @param jdbcTemplate The JdbcTemplate to use to run queries.
     */
    public AlbumDao(final JdbcTemplate jdbcTemplate) {

        Assert.notNull(jdbcTemplate, "JdbcTemplate cannot be null.");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insert(final Collection<? extends Album> toInsert) {

        final int[] rowsInserted = this.jdbcTemplate.batchUpdate(INSERT_SQL, new AlbumInsert(toInsert));

        return Arrays.stream(rowsInserted).sum();
    }

    @Override
    public int update(final Collection<? extends Album> toUpdate) {

        final int[] rowsUpdated = this.jdbcTemplate.batchUpdate(UPDATE_SQL, new AlbumUpdate(toUpdate));

        return Arrays.stream(rowsUpdated).sum();
    }
}
