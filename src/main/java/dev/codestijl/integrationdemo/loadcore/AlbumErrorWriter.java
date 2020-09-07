package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.dao.AlbumErrorDao;
import dev.codestijl.integrationdemo.entity.AlbumError;

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
 * Writes processing errors to the ALBUM_ERROR table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumErrorWriter implements ItemWriter<CoreAlbumWrapper>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(AlbumErrorWriter.class);

    private final AlbumErrorDao albumErrorDao;

    private int rowsInserted;

    /**
     * Constructs a new AlbumErrorWriter.
     *
     * @param dataSource The DataSource to use to run queries.
     */
    public AlbumErrorWriter(final DataSource dataSource) {

        this.albumErrorDao = new AlbumErrorDao(new JdbcTemplate(dataSource));
    }

    @Override
    public void write(final List<? extends CoreAlbumWrapper> items) {

        // Pull all the error records out of the list.
        final List<AlbumError> records = items.stream()
                .filter(CoreAlbumWrapper::isInError)
                .map(CoreAlbumWrapper::getAlbumError)
                .collect(Collectors.toList());

        if (records.isEmpty()) {
            return;
        }

        logger.debug(String.format("Writing batch of %,d errors.", records.size()));

        // Delegate saving them to the DAO.
        this.rowsInserted += this.albumErrorDao.insert(records);
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.rowsInserted = 0;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        logger.info(String.format("%,d errors inserted.", this.rowsInserted));

        return ExitStatus.COMPLETED;
    }
}
