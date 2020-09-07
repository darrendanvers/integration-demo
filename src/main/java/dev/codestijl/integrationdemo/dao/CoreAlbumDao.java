package dev.codestijl.integrationdemo.dao;

import dev.codestijl.integrationdemo.common.SingleResultReader;
import dev.codestijl.integrationdemo.entity.CoreAlbum;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

/**
 * DAO for working wth the CT_ALBUM table in the CORE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class CoreAlbumDao implements Dao<CoreAlbum> {

    private static final String INSERT_SQL = "INSERT INTO CORE.CT_ALBUM " +
            "(ALBUM_ID, GTIN_14, ALBUM_NAME, ARTIST_NAME, CREATE_TIME, LAST_UPDATE_TIME, SOURCE_ALBUM_ID) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL = "UPDATE CORE.CT_ALBUM SET GTIN_14 = ?, ALBUM_NAME = ?, ARTIST_NAME = ?, " +
            "LAST_UPDATE_TIME = ?, SOURCE_ALBUM_ID = ? WHERE ALBUM_ID = ?";

    private static final String SELECT_SQL = "SELECT ALBUM_ID, GTIN_14, ALBUM_NAME, ARTIST_NAME, SOURCE_ALBUM_ID " +
            "FROM CORE.CT_ALBUM";

    private static final RowMapper<CoreAlbum> ROW_MAPPER = (rs, rowNum) ->

        new CoreAlbum().setAlbumId(rs.getString("ALBUM_ID"))
                .setAlbumName(rs.getString("ALBUM_NAME"))
                .setArtistName(rs.getString("ARTIST_NAME"))
                .setGtin14(rs.getString("GTIN_14"))
                .setSourceAlbumId(rs.getString("SOURCE_ALBUM_ID"));

    private static final SingleResultReader<CoreAlbum> SINGLE_RESULT_READER = new SingleResultReader<>(ROW_MAPPER);

    private final JdbcTemplate jdbcTemplate;

    /**
     * BatchPreparedStatementSetter to insert rows in the CT_ALBUM table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class CoreAlbumInsert extends BaseBatchPreparedStatementSetter<CoreAlbum> {

        private CoreAlbumInsert(final Collection<? extends CoreAlbum> data) {
            super(data);
        }

        @Override
        protected void doSetValues(final PreparedStatement preparedStatement, final CoreAlbum value) throws SQLException {

            final Instant now = Instant.now();

            preparedStatement.setString(1, value.getAlbumId());
            preparedStatement.setString(2, value.getGtin14());
            preparedStatement.setString(3, value.getAlbumName());
            preparedStatement.setString(4, value.getArtistName());
            preparedStatement.setTimestamp(5, Timestamp.from(now));
            preparedStatement.setTimestamp(6, Timestamp.from(now));
            preparedStatement.setString(7, value.getSourceAlbumId());
        }
    }

    /**
     * BatchPreparedStatementSetter to update rows in the CT_ALBUM table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class CoreAlbumUpdate extends BaseBatchPreparedStatementSetter<CoreAlbum> {

        private CoreAlbumUpdate(final Collection<? extends CoreAlbum> data) {
            super(data);
        }

        @Override
        protected void doSetValues(final PreparedStatement preparedStatement, final CoreAlbum value) throws SQLException {

            final Instant now = Instant.now();

            preparedStatement.setString(1, value.getGtin14());
            preparedStatement.setString(2, value.getAlbumName());
            preparedStatement.setString(3, value.getArtistName());
            preparedStatement.setTimestamp(4, Timestamp.from(now));
            preparedStatement.setString(5, value.getSourceAlbumId());
            preparedStatement.setString(6, value.getAlbumId());
        }
    }

    /**
     * Constructs a new CoreAlbumDao.
     *
     * @param jdbcTemplate The JdbcTemplate to use to run queries.
     */
    public CoreAlbumDao(final JdbcTemplate jdbcTemplate) {

        Assert.notNull(jdbcTemplate, "JdbcTemplate cannot be null.");
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds a CoreAlbum in the CT_ALBUM table with a specific GTIN-14.
     *
     * @param gtin14 The GTIN-14 to look for in the CT_ALBUM table.
     * @return A CoreAlbum with the padded in GTIN-14 or empty if not found.
     */
    public Optional<CoreAlbum> findByGtin14(final String gtin14) {

        return Optional.ofNullable(this.jdbcTemplate.query(SELECT_SQL + " WHERE GTIN_14 = ?",
                this.argsAsArray(gtin14),
                SINGLE_RESULT_READER));
    }

    @Override
    public int insert(final Collection<? extends CoreAlbum> toInsert) {

        final int[] rowsInserted = this.jdbcTemplate.batchUpdate(INSERT_SQL, new CoreAlbumInsert(toInsert));
        return Arrays.stream(rowsInserted).sum();
    }

    @Override
    public int update(final Collection<? extends CoreAlbum> toUpdate) {

        final int[] rowsInserted = this.jdbcTemplate.batchUpdate(UPDATE_SQL, new CoreAlbumUpdate(toUpdate));
        return Arrays.stream(rowsInserted).sum();
    }
}
