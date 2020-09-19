package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.Status;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests AlbumToCoreProcessor.
 *
 * @author darren
 * @since 1.0.0
 */
// This file beings all test GTINs with 99.
@SpringBootTest
public class AlbumToCoreProcessorTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Calls process with the same GTIN twice inside the same chunk. This
     * should return the first CoreAlbumWrapper being marked for
     * insert and the second being marked for update.
     */
    @Test
    public void process_sameGtinInBatch_returnsOneInsertAndOneUpdate() {

        final AlbumToCoreProcessor albumToCoreProcessor = new AlbumToCoreProcessor(this.dataSource);

        final Album testAlbum = new Album().setAlbumId("23432343223")
                .setAlbumName("album one")
                .setArtist("artist one")
                .setBatchId("23423423423")
                .setGtin14("99203927593820")
                .setStatus(Status.PENDING);

        albumToCoreProcessor.beforeStep(Mockito.mock(StepExecution.class));
        albumToCoreProcessor.beforeChunk(Mockito.mock(ChunkContext.class));

        final CoreAlbumWrapper firstWrapper = albumToCoreProcessor.process(testAlbum);
        Assert.assertNotNull(firstWrapper);
        Assert.assertTrue(firstWrapper.isInsert());

        testAlbum.setAlbumName("updated name");

        final CoreAlbumWrapper secondWrapper = albumToCoreProcessor.process(testAlbum);
        Assert.assertNotNull(secondWrapper);
        Assert.assertFalse(secondWrapper.isInsert());
        Assert.assertEquals(testAlbum.getAlbumName(), secondWrapper.getCoreAlbum().getAlbumName());
    }
}
