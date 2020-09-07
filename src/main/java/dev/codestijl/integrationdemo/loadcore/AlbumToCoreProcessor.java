package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.common.CollectionUtils;
import dev.codestijl.integrationdemo.common.IdUtils;
import dev.codestijl.integrationdemo.common.ProgressLogger;
import dev.codestijl.integrationdemo.common.ValidationException;
import dev.codestijl.integrationdemo.dao.CoreAlbumDao;
import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.AlbumError;
import dev.codestijl.integrationdemo.entity.CoreAlbum;
import dev.codestijl.integrationdemo.entity.Status;

import java.util.*;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * Processor that will take ALBUMS from the STAGE schema and prepare them to be written to the CORE schema.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumToCoreProcessor implements ItemProcessor<Album, CoreAlbumWrapper>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(AlbumToCoreProcessor.class);
    private static final int LOG_AT = 500;
    private static final int ERROR_TEXT_MAX_LENGTH = 1_000;

    private final CoreAlbumDao coreAlbumDao;

    private final ProgressLogger progressLogger = ProgressLogger.builder()
            .setLogger(logger)
            .setLogAt(LOG_AT)
            .build();

    private final Set<String> processedGtinList = new HashSet<>();
    private final CoreAlbumValidator coreAlbumValidator = new CoreAlbumValidator();

    /**
     * Creates a new AlbumToCoreProcessor.
     *
     * @param dataSource The DataSource to run queries with.
     */
    public AlbumToCoreProcessor(final DataSource dataSource) {

        Assert.notNull(dataSource, "DataSource cannot be null.");

        this.coreAlbumDao = new CoreAlbumDao(new JdbcTemplate(dataSource));
    }

    @Override
    public CoreAlbumWrapper process(final Album stageAlbum) {

        this.progressLogger.incrementCount();

        final Optional<CoreAlbum> existingAlbum = this.coreAlbumDao.findByGtin14(stageAlbum.getGtin14());

        // If we don't get a result back, then we need to insert a new record.
        final boolean isInsert = existingAlbum.isEmpty();

        // If we found a match in the core DB, use that one. If not, make a new album.
        // In both cases, overwrite whatever was in the object with the data from the
        // staging DB.
        final CoreAlbum coreAlbum = overlayCoreAlbum(stageAlbum, existingAlbum.orElse(new CoreAlbum().setAlbumId(IdUtils.newId())));

        try {
            // Validate what will go into the DB.
            this.coreAlbumValidator.validate(coreAlbum);

            // A GTIN can only be in the file once, so keep track of the ones
            // that have been processed already.
            this.processGtinOnce(stageAlbum.getGtin14());

            // If we get here, it validated and is ready for insert/update.
            stageAlbum.setStatus(Status.COMPLETE);

            return new CoreAlbumWrapper(coreAlbum, isInsert, stageAlbum);

        } catch (ValidationException e) {

            // If we get here, there was an error validating.
            stageAlbum.setStatus(Status.ERROR);
            final AlbumError error = new AlbumError().setErrorId(IdUtils.newId())
                    .setAlbumId(stageAlbum.getAlbumId())
                    .setBatchId(stageAlbum.getBatchId())
                    .setErrorText(CollectionUtils.asString(e.getErrors(), ERROR_TEXT_MAX_LENGTH));

            return new CoreAlbumWrapper(error, stageAlbum);
        }
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.processedGtinList.clear();
        this.progressLogger.reset();
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        this.progressLogger.log();
        return ExitStatus.COMPLETED;
    }

    private static CoreAlbum overlayCoreAlbum(final Album album, final CoreAlbum coreAlbum) {

        return coreAlbum.setGtin14(album.getGtin14())
                .setArtistName(album.getArtist())
                .setAlbumName(album.getAlbumName())
                .setSourceAlbumId(album.getAlbumId());
    }

    private void processGtinOnce(final String gtin14) throws ValidationException {

        if (this.processedGtinList.contains(gtin14)) {
            throw new ValidationException("Error in file format.",
                    String.format("The GTIN %s occurs more than once in the file.", gtin14));
        }

        this.processedGtinList.add(gtin14);
    }
}
