package dev.codestijl.integrationdemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a record in the CT_ALBUM table in the CORE schema.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class CoreAlbum {

    private String albumId;
    private String gtin14;
    private String albumName;
    private String artistName;
    private String sourceAlbumId;

    @Override
    public String toString() {
        return String.format("{%s,%s,'%s'}", this.albumId, this.gtin14, this.albumName);
    }
}
