package dev.codestijl.integrationdemo.loadalbum;

import dev.codestijl.integrationdemo.dao.AlbumDao;
import dev.codestijl.integrationdemo.dao.SongDao;
import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.Song;

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
import org.springframework.util.Assert;

/**
 * Writes Albums and the Songs inside them to the staging tables.
 *
 * @author darren
 * @since 1.0.0
 */
public class AlbumWriter implements ItemWriter<Album>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(AlbumWriter.class);

    private final AlbumDao albumDao;
    private final SongDao songDao;

    private int albumsSaved;
    private int songsSaved;

    /**
     * Constructs a new AlbumWriter.
     *
     * @param dataSource The DataSource to use to run queries.
     */
    public AlbumWriter(final DataSource dataSource) {

        Assert.notNull(dataSource, "DataSource cannot be null");

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        this.albumDao = new AlbumDao(jdbcTemplate);
        this.songDao = new SongDao(jdbcTemplate);
    }

    @Override
    public void write(final List<? extends Album> albums) {

        logger.debug(String.format("Writing batch of %,d albums.", albums.size()));

        // Extract all the songs in all the Albums into a single list.
        final List<Song> songs = albums.stream()
                .map(Album::getSongs)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Delegate the saves to the DAOs
        this.albumsSaved += this.albumDao.insert(albums);
        this.songsSaved += this.songDao.insert(songs);
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.albumsSaved = 0;
        this.songsSaved = 0;
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        logger.info(String.format("%,d albums and %,d songs saved.", this.albumsSaved, this.songsSaved));
        return ExitStatus.COMPLETED;
    }
}
