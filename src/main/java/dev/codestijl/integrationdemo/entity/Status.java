package dev.codestijl.integrationdemo.entity;

import java.util.Objects;

import lombok.Getter;

/**
 * The processing status of a record.
 *
 * @author darren
 * @since 1.0.0
 */
@Getter
public enum Status {

    PENDING("PEND"),      // The record has been saved and not picked up for processing.
    COMPLETE("COMP"),     // The record has been processed successfully.
    ERROR("ERROR");       // The record has been processed, but there was an error.

    private final String id;

    /**
     * Constructs a new Status.
     *
     * @param id The ID of the Status in the STATUS table.
     */
    Status(final String id) {
        this.id = id;
    }

    /**
     * Returns a Status with a given value.
     *
     * @param value The Status ID value. This value comes from the STATUS_CD in the STATUS table.
     * @return A Status with a given ID.
     * @throws IllegalArgumentException If an ID that does not match any status is passed in.
     */
    public static Status of(final String value) {

        for (final Status status : Status.values()) {
            if (Objects.equals(status.id, value)) {
                return status;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' is not a valid status.", value));
    }
}
