package dev.codestijl.integrationdemo.entity;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a record in the ALBUM table in the STAGE schema.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
@Setter
@Accessors(chain = true)
public class Album {

    // Tracking fields.
    private String albumId;
    private String batchId;
    private Status status;

    // Data fields.
    @JsonProperty("gtin-14")
    private String gtin14;
    private String albumName;
    private String artist;
    private final List<Song> songs = new LinkedList<>();

    @Override
    public String toString() {

        return String.format("{%s, %s, '%s'}", this.albumId, this.gtin14, this.albumName);
    }
}
