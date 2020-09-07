package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.dao.AlbumDao;
import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.Status;

import javax.sql.DataSource;

import org.springframework.batch.item.database.JdbcCursorItemReader;

/**
 * Reads Albums from the ALBUM table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumReader extends JdbcCursorItemReader<Album> {

    /**
     * Constructs a new AlbumReader.
     *
     * @param dataSource The DataSource to use to run queries.
     * @param batchId The ID of the batch being processed.
     */
    public AlbumReader(final DataSource dataSource, final String batchId) {

        super();

        // This class is a thin wrapper around a JdbcCursorItemReader that just sets
        // a few default properties.
        this.setDataSource(dataSource);
        this.setSql(AlbumDao.SELECT_SQL + " WHERE BATCH_ID = ? AND STATUS_CD = ?");
        this.setPreparedStatementSetter((ps) -> {
            ps.setString(1, batchId);
            ps.setString(2, Status.PENDING.getId());
        });
        this.setRowMapper(AlbumDao.ROW_MAPPER);
    }
}
