package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.AlbumError;
import dev.codestijl.integrationdemo.entity.CoreAlbum;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Tests CoreAlbumWrapper.
 *
 * @author darren
 * @since 1.0.0
 */
public class CoreAlbumWrapperTest {

    /**
     * Calls the constructor for CoreAlbumWrapper. It will pass in a core album and true for insert.
     * It should not be in error and isInsert should return true. Calling getAlbumError should
     * throw an error and getCoreAlbum should not.
     */
    @Test
    public void coreAlbumWrapper_withCoreAlbum_insert_notInError() {

        final CoreAlbumWrapper coreAlbumWrapper = new CoreAlbumWrapper(new CoreAlbum(), true, new Album());

        Assert.assertTrue(coreAlbumWrapper.notInError());
        Assert.assertFalse(coreAlbumWrapper.isInError());
        Assert.assertTrue(coreAlbumWrapper.isInsert());
        Assert.assertThrows(IllegalStateException.class, coreAlbumWrapper::getAlbumError);
        Assert.assertNotNull(coreAlbumWrapper.getCoreAlbum());
    }

    /**
     * Calls the constructor for CoreAlbumWrapper. It will pass in a core album and false for insert.
     * It should not be in error and isInsert should return false. Calling getAlbumError should
     * throw an error and getCoreAlbum should not.
     */
    @Test
    public void coreAlbumWrapper_withCoreAlbum_notInsert_notInError() {

        final CoreAlbumWrapper coreAlbumWrapper = new CoreAlbumWrapper(new CoreAlbum(), false, new Album());

        Assert.assertTrue(coreAlbumWrapper.notInError());
        Assert.assertFalse(coreAlbumWrapper.isInError());
        Assert.assertFalse(coreAlbumWrapper.isInsert());
        Assert.assertThrows(IllegalStateException.class, coreAlbumWrapper::getAlbumError);
        Assert.assertNotNull(coreAlbumWrapper.getCoreAlbum());
    }

    /**
     * Calls the constructor for CoreAlbumWrapper. It will pass in an AlbumError.
     * It should be in error. Calling getCoreAlbum should throw an error and getAlbumError should not.
     */
    @Test
    public void coreAlbumWrapper_withAlbumError_isInError() {

        final CoreAlbumWrapper coreAlbumWrapper = new CoreAlbumWrapper(new AlbumError(), new Album());

        Assert.assertFalse(coreAlbumWrapper.notInError());
        Assert.assertTrue(coreAlbumWrapper.isInError());
        Assert.assertThrows(IllegalStateException.class, coreAlbumWrapper::getCoreAlbum);
        Assert.assertNotNull(coreAlbumWrapper.getAlbumError());
    }
}
