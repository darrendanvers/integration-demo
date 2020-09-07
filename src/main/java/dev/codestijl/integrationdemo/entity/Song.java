package dev.codestijl.integrationdemo.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a record in the SONG table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class Song {

    // Tracking fields.
    private String songId;
    private String albumId;

    // Data field.
    private String songName;

    @Override
    public String toString() {
        return String.format("{%s,'%s'}", this.songId, this.songName);
    }
}
