package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.entity.Album;
import dev.codestijl.integrationdemo.entity.AlbumError;
import dev.codestijl.integrationdemo.entity.CoreAlbum;

import java.util.Objects;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

/**
 * Wraps the objects that will need to be updated or inserted in the DB after
 * being processed during this step.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
@Accessors(chain = true)
public class CoreAlbumWrapper {

    private final Album stageAlbum;
    private final boolean isInsert;
    private final CoreAlbum coreAlbum;
    private final AlbumError albumError;

    /**
     * Constructs a new CoreAlbumWrapper. In this overloading, the wrapper will hold a CoreAlbum to insert
     * or update in the DB.
     *
     * @param coreAlbum The CoreAlbum to insert or update.
     * @param isInsert Should the CoreAlbum be inserted? Pass false for an update.
     * @param stageAlbum The Album to update the status of in the STAGE schema.
     */
    public CoreAlbumWrapper(final CoreAlbum coreAlbum, final boolean isInsert, final Album stageAlbum) {

        this(coreAlbum, isInsert, null, stageAlbum);

        Assert.notNull(coreAlbum, "CoreAlbum cannot be null.");
    }

    /**
     * Constructs a new CoreAlbumWrapper. In this overloading, the wrapper will hold a an AlbumError to insert
     * into the STAGE schema.
     *
     * @param albumError The AlbumError to insert.
     * @param stageAlbum The Album to update the status of in the STAGE schema.
     */
    public CoreAlbumWrapper(final AlbumError albumError, final Album stageAlbum) {

        this(null, false, albumError, stageAlbum);

        Assert.notNull(albumError, "AlbumError cannot be null.");
    }

    private CoreAlbumWrapper(final CoreAlbum coreAlbum, final boolean isInsert, final AlbumError albumError, final Album stageAlbum) {

        Assert.notNull(stageAlbum, "Album cannot be null.");

        this.stageAlbum = stageAlbum;
        this.isInsert = isInsert;
        this.coreAlbum = coreAlbum;
        this.albumError = albumError;
    }

    /**
     * Returns the AlbumError this object holds.
     *
     * @return The AlbumError this object holds.
     * @throws IllegalStateException If the object does not hold an AlbumError.
     */
    public AlbumError getAlbumError() {

        if (this.notInError()) {
            throw new IllegalStateException("This wrapper does not hold an error.");
        }

        return this.albumError;
    }

    /**
     * Returns the CoreAlbum this object holds.
     *
     * @return The AlbumError this object holds.
     * @throws IllegalStateException If the object does not hold a CoreAlbum.
     */
    public CoreAlbum getCoreAlbum() {

        if (this.isInError()) {
            throw new IllegalStateException("This wrapper does not hold a valid CoreAlbum.");
        }

        return this.coreAlbum;
    }

    /**
     * Returns true when this class holds a valid CoreAlbum to insert or update in the DB. If it returns false,
     * then the object holds an error.
     *
     * @return True if this object holds a CoreAlbum and false otherwise.
     */
    public boolean notInError() {
        return Objects.nonNull(this.coreAlbum);
    }

    /**
     * Returns true when this class holds an AlbumError to insert into the DB. If it returns false,
     * then the object holds a valid CoreAlbum.
     *
     * @return True if this object holds a AlbumError and false otherwise.
     */
    public boolean isInError() {
        return Objects.nonNull(this.albumError);
    }
}
