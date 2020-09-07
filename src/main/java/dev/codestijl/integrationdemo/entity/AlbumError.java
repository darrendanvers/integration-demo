package dev.codestijl.integrationdemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a record in the ALBUM_ERROR table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class AlbumError {

    // Tracking fields.
    private String errorId;
    private String albumId;
    private String batchId;

    // Data field.
    private String errorText;

    @Override
    public String toString() {

        return String.format("{%s, '%s'}", this.errorId, this.errorText);
    }
}
