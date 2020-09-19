package dev.codestijl.integrationdemo.dao;

import dev.codestijl.integrationdemo.common.IdUtils;
import dev.codestijl.integrationdemo.entity.AlbumError;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Tests AlbumErrorDao.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootTest
public class AlbumErrorDaoTest {

    // This is in the test data.
    private static final String BATCH_ID = "bb439ecb-776b-457a-9ab8-759687193958";
    private static final String ALBUM_ID = "6ddc6095-6979-4132-8e1f-f88472f55f8c";

    @Autowired
    private DataSource dataSource;

    /**
     * Tests inserting a single AlbumError.
     */
    @Test
    public void insert_insertsSingleRecord() {

        final String errorId = IdUtils.newId();

        final AlbumError albumError = albumErrorFrom(errorId);

        final AlbumErrorDao albumErrorDao = new AlbumErrorDao(new JdbcTemplate(this.dataSource));

        final int rowsInserted = albumErrorDao.insert(albumError);
        Assert.assertEquals(1, rowsInserted);

        checkAlbumErrorById(albumErrorDao, errorId);
    }

    /**
     * Tests inserting a list of AlbumErrors.
     */
    @Test
    public void insert_insertsMultipleRecords() {

        final String[] errorIds = {IdUtils.newId(), IdUtils.newId(), IdUtils.newId(), IdUtils.newId()};

        final List<AlbumError> albumErrors = Arrays.stream(errorIds)
                .map(AlbumErrorDaoTest::albumErrorFrom)
                .collect(Collectors.toList());

        final AlbumErrorDao albumErrorDao = new AlbumErrorDao(new JdbcTemplate(this.dataSource));

        final int rowsInserted = albumErrorDao.insert(albumErrors);
        Assert.assertEquals(errorIds.length, rowsInserted);

        Arrays.stream(errorIds)
                .forEach(e -> checkAlbumErrorById(albumErrorDao, e));
    }

    private static String errorTextFrom(final String errorId) {
        return String.format("ERROR TEXT FOR %s.", errorId);
    }

    private static AlbumError albumErrorFrom(final String errorId) {

        return new AlbumError()
                .setErrorId(errorId)
                .setAlbumId(ALBUM_ID)
                .setBatchId(BATCH_ID)
                .setErrorText(errorTextFrom(errorId));
    }

    private static void checkAlbumErrorById(final AlbumErrorDao albumErrorDao, final String errorId) {

        final Optional<AlbumError> albumError = albumErrorDao.findById(errorId);

        Assert.assertTrue(albumError.isPresent());

        Assert.assertEquals(errorId, albumError.get().getErrorId());
        Assert.assertEquals(ALBUM_ID, albumError.get().getAlbumId());
        Assert.assertEquals(BATCH_ID, albumError.get().getBatchId());
        Assert.assertEquals(errorTextFrom(errorId), albumError.get().getErrorText());
        Assert.assertTrue(albumError.get().getCreateTime().isAfter(Instant.now().minus(1, ChronoUnit.MINUTES)));
    }
}
