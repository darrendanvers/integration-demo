package dev.codestijl.integrationdemo.loadalbum;

import dev.codestijl.integrationdemo.common.ClobJsonReader;
import dev.codestijl.integrationdemo.entity.Album;

import javax.sql.DataSource;

/**
 * Reads Albums from the CLOB field in the BATCH table.
 *
 * @author darren
 * @since 1.0.0
 */
public class ClobAlbumReader extends ClobJsonReader<Album> {

    private static final String SELECT_SQL = "SELECT PAYLOAD FROM STAGE.BATCH WHERE BATCH_ID = ?";

    /**
     * Constructs a new ClobAlbumReader.
     *
     * @param dataSource The DataSource to run queries with.
     * @param batchId The batch ID to process.
     */
    public ClobAlbumReader(final DataSource dataSource, final String batchId) {

        super(dataSource, SELECT_SQL, Album.class, (ps) -> ps.setString(1, batchId));
    }
}
