package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.dao.AlbumDao;
import dev.codestijl.integrationdemo.entity.Album;

import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Updates the status of records in the ALBUM table in the STAGE schema after they have been processed.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumWriter implements ItemWriter<CoreAlbumWrapper>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(AlbumWriter.class);

    private final AlbumDao albumDao;

    private int rowsUpdated;

    /**
     * Constructs a new AlbumWriter.
     *
     * @param dataSource The DataSource to use to run queries.
     */
    public AlbumWriter(final DataSource dataSource) {

        this.albumDao = new AlbumDao(new JdbcTemplate(dataSource));
    }

    @Override
    public void write(final List<? extends CoreAlbumWrapper> items) {

        final List<Album> albums = items.stream()
                .map(CoreAlbumWrapper::getStageAlbum)
                .collect(Collectors.toList());

        logger.debug(String.format("Updating batch of %,d albums.", albums.size()));

        this.rowsUpdated += this.albumDao.update(albums);
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.rowsUpdated = 0;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        logger.info(String.format("%,d rows updated.", this.rowsUpdated));

        return ExitStatus.COMPLETED;
    }
}
