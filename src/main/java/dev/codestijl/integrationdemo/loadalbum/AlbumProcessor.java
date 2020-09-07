package dev.codestijl.integrationdemo.loadalbum;

import dev.codestijl.integrationdemo.common.IdUtils;
import dev.codestijl.integrationdemo.common.ProgressLogger;
import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.Song;
import dev.codestijl.integrationdemo.entity.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.Assert;

/**
 * Processes an Album that was read from the CLOB and prepares it to be stored in the ALBUM table.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumProcessor implements ItemProcessor<Album, Album>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(AlbumProcessor.class);
    private static final int LOG_AT = 500;

    private final String batchId;
    private final ProgressLogger progressLogger = ProgressLogger.builder()
            .setLogger(logger)
            .setLogAt(LOG_AT)
            .build();

    /**
     * Utility class to set the IDs in a Song.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class SongProcessor {

        private final String albumId;

        /**
         * Constructs a new SongProcessor.
         *
         * @param albumId The albumId to assign to each Song.
         */
        public SongProcessor(final String albumId) {
            this.albumId = albumId;
        }

        /**
         * Populates all the IDs in a Song.
         *
         * @param song The Song to set the IDs in.
         */
        public void setIds(final Song song) {

            song.setSongId(IdUtils.newId())
                    .setAlbumId(this.albumId);
        }
    }

    /**
     * Constructs a new AlbumProcessor.
     *
     * @param batchId The batchId to set in each Album processed.
     */
    public AlbumProcessor(final String batchId) {

        Assert.notNull(batchId, "Batch ID cannot be null");

        this.batchId = batchId;
    }

    @Override
    public Album process(final Album album) {

        this.progressLogger.incrementCount();

        final String albumId = IdUtils.newId();

        // Set the IDs in the Songs inside the Album.
        final SongProcessor songProcessor = new SongProcessor(albumId);
        album.getSongs().forEach(songProcessor::setIds);

        // Set the IDs and status of the Album.
        return album.setAlbumId(albumId)
                .setBatchId(this.batchId)
                .setStatus(Status.PENDING);
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.progressLogger.reset();
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        this.progressLogger.log();
        return ExitStatus.COMPLETED;
    }
}
