package dev.codestijl.integrationdemo.dao;

import dev.codestijl.integrationdemo.common.IdUtils;
import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.Status;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Tests AlbumDao.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootTest
public class AlbumDaoTest {

    // This is in the test data.
    private static final String BATCH_ID = "bb439ecb-776b-457a-9ab8-759687193958";

    @Autowired
    private DataSource dataSource;

    /**
     * Calls insert passing in only one Album. This will fetch it back and the make sure
     * all the values are set.
     */
    @Test
    public void insert_savesSingleAlbum() {

        final AlbumDao albumDao = new AlbumDao(new JdbcTemplate(this.dataSource));

        final String albumId = IdUtils.newId();

        final Album album = new Album().setAlbumId(albumId)
                .setAlbumName("test album 1")
                .setArtist("test artist 1")
                .setBatchId(BATCH_ID)
                .setGtin14("98837456178392")
                .setStatus(Status.COMPLETE);

        final int rowsInserted = albumDao.insert(List.of(album));
        Assert.assertEquals(1, rowsInserted);

        final Optional<Album> toCompare = albumDao.findById(albumId);

        Assert.assertTrue(toCompare.isPresent());

        Assert.assertEquals(albumId, toCompare.get().getAlbumId());
        Assert.assertEquals("test album 1", toCompare.get().getAlbumName());
        Assert.assertEquals("test artist 1", toCompare.get().getArtist());
        Assert.assertEquals(BATCH_ID, toCompare.get().getBatchId());
        Assert.assertEquals("98837456178392", toCompare.get().getGtin14());
        Assert.assertEquals(Status.COMPLETE, toCompare.get().getStatus());
        Assert.assertEquals(toCompare.get().getCreateTime(), toCompare.get().getLastUpdateTime());
        Assert.assertTrue(toCompare.get().getCreateTime().isAfter(Instant.now().minus(1, ChronoUnit.MINUTES)));
    }

    /**
     * Calls insert passing in a list of Albums. This one does not check all the values, just that the full
     * list is inserted.
     */
    @Test
    // The whole point of the loop is to instantiate objects.
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void insert_savesMultipleAlbums() {

        final AlbumDao albumDao = new AlbumDao(new JdbcTemplate(this.dataSource));

        final String[] albumIds = {IdUtils.newId(), IdUtils.newId(), IdUtils.newId(), IdUtils.newId()};

        final List<Album> toInsert = new LinkedList<>();

        for (int i = 0; i < albumIds.length; i++) {
            toInsert.add(new Album().setAlbumId(albumIds[i])
                    .setAlbumName(String.format("test album %d", i))
                    .setArtist(String.format("test artist %d", i))
                    .setBatchId(BATCH_ID)
                    .setGtin14(Long.toString(98_837_456_178_390L + i))
                    .setStatus(Status.COMPLETE));
        }

        final int rowsInserted = albumDao.insert(toInsert);
        Assert.assertEquals(albumIds.length, rowsInserted);

        Arrays.stream(albumIds).forEach(a -> Assert.assertTrue(albumDao.findById(a).isPresent()));
    }

    /**
     * Calls update passing in a single Album. This will make sure the status is updated (the only updatable
     * field) and the last update time is adjusted.
     */
    @Test
    public void update_updatesSingleAlbum() {

        final String albumId = "6ddc6095-6979-4132-8e1f-f88472f55f8c";

        final AlbumDao albumDao = new AlbumDao(new JdbcTemplate(this.dataSource));

        final Album toUpdate = updateAlbumStatusById(albumDao, albumId, Status.ERROR);
        final int updateCount = albumDao.update(List.of(toUpdate));

        Assert.assertEquals(1, updateCount);

        checkAlbumStatusUpdateById(albumDao, albumId, Status.ERROR);
    }

    /**
     * Calls update passing in a list of Albums. This will make sure the status is updated (the only updatable
     * field) and the last update time is adjusted.
     */
    @Test
    public void update_updatesMultipleAlbums() {

        final String[] albumIds = {
            "8299a1f3-1194-4b20-85fb-ced161ac67ec",
            "e3a9a73e-288b-470b-bd1d-5cd1b35bf99b",
            "3bee4104-441c-48da-956a-cdfcd3e77b43",
            "5abc0800-86d5-47b3-b80a-e4a939d9db84"
        };

        final AlbumDao albumDao = new AlbumDao(new JdbcTemplate(this.dataSource));

        final List<Album> toUpdate = new LinkedList<>();

        Arrays.stream(albumIds)
                .map(a -> updateAlbumStatusById(albumDao, a, Status.PENDING))
                .forEach(toUpdate::add);

        final int updateCount = albumDao.update(toUpdate);
        Assert.assertEquals(4, updateCount);

        Arrays.stream(albumIds)
                .forEach(a -> checkAlbumStatusUpdateById(albumDao, a, Status.PENDING));
    }

    private static Album updateAlbumStatusById(final AlbumDao albumDao, final String albumId, final Status newStatus) {

        final Album album = albumDao.findById(albumId)
                .orElseThrow(() -> new IllegalStateException(String.format("Album %s is missing from test data.", albumId)));
        return album.setStatus(newStatus);
    }

    private static void checkAlbumStatusUpdateById(final AlbumDao albumDao, final String albumId, final Status expectedStatus) {

        final Album album = albumDao.findById(albumId)
                .orElseThrow(() -> new IllegalStateException(String.format("Album %s is missing from test data after update.", albumId)));

        Assert.assertEquals(expectedStatus, album.getStatus());
        Assert.assertTrue(album.getCreateTime().isBefore(album.getLastUpdateTime()));
        // I guess not 100% guaranteed, but if it take more than a minute to get this, there's
        // likely a problem.
        Assert.assertTrue(album.getLastUpdateTime().isAfter(Instant.now().minus(1, ChronoUnit.MINUTES)));
    }
}
