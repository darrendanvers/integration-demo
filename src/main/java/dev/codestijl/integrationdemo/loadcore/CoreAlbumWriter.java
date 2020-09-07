package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.dao.CoreAlbumDao;
import dev.codestijl.integrationdemo.entity.CoreAlbum;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
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
 * Writes CoreAlbums to the CT_ALBUM table in the CORE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class CoreAlbumWriter implements ItemWriter<CoreAlbumWrapper>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(CoreAlbumWriter.class);

    private final CoreAlbumDao coreAlbumDao;

    private int rowsInserted;
    private int rowsUpdated;

    /**
     * Constructs a new CoreAlbumWriter.
     *
     * @param dataSource The DataSource to use to run queries.
     */
    public CoreAlbumWriter(final DataSource dataSource) {

        this.coreAlbumDao = new CoreAlbumDao(new JdbcTemplate(dataSource));
    }

    @Override
    public void write(final List<? extends CoreAlbumWrapper> items) {

        this.rowsInserted += this.processStream(items, CoreAlbumWrapper::isInsert, this.coreAlbumDao::insert);
        this.rowsUpdated += this.processStream(items, (c) -> !c.isInsert(), this.coreAlbumDao::update);
    }

    private int processStream(final List<? extends CoreAlbumWrapper> items, final Predicate<CoreAlbumWrapper> filter,
                              final Function<List<? extends CoreAlbum>, Integer> processor) {

        final List<CoreAlbum> records = items.stream()
                .filter(CoreAlbumWrapper::notInError)
                .filter(filter)
                .map(CoreAlbumWrapper::getCoreAlbum)
                .collect(Collectors.toList());

        logger.debug(String.format("Writing batch of %,d albums to the core DB.", records.size()));

        return processor.apply(records);
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.rowsInserted = 0;
        this.rowsUpdated = 0;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        logger.info(String.format("%,d albums inserted and %,d updated.", this.rowsInserted, this.rowsUpdated));

        return ExitStatus.COMPLETED;
    }
}
