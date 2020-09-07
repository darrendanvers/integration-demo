package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.common.ValidationException;
import dev.codestijl.integrationdemo.entity.CoreAlbum;

import java.util.*;

/**
 * Validates a CoreAlbum is valid and can be saved in the CORE schema.
 *
 * @author darren
 * @since 1.0.0
 */
/* default */ class CoreAlbumValidator {

    private static final int GTIN_14_LENGTH = 14;
    private static final int ALBUM_NAME_MAX_LENGTH = 100;
    private static final int ARTIST_NAME_MAX_LENGTH = 100;

    /**
     * Validates a CoreAlbum.
     *
     * @param coreAlbum The CoreAlbum to validate.
     * @throws ValidationException Any error in validation will throw a ValidationException. The validation will
     *                             be as exhaustive as possible and all errors will be returned.
     */
    public void validate(final CoreAlbum coreAlbum) throws ValidationException {

        final List<String> errors = new LinkedList<>();

        this.validateGtin14(coreAlbum.getGtin14()).ifPresent(errors::add);
        this.validateAlbumName(coreAlbum.getAlbumName()).ifPresent(errors::add);
        this.validateArtistName(coreAlbum.getArtistName()).ifPresent(errors::add);
        this.validateSourceAlbum(coreAlbum.getSourceAlbumId()).ifPresent(errors::add);
        this.validateAlbumId(coreAlbum.getAlbumId()).ifPresent(errors::add);

        if (!errors.isEmpty()) {
            throw new ValidationException("Unable to validate album.", errors);
        }
    }

    private Optional<String> validateGtin14(final String gtin14) {

        if (Objects.isNull(gtin14)) {
            return Optional.of("GTIN-14 name is required.");
        } else if (gtin14.length() != GTIN_14_LENGTH) {
            return Optional.of("GTIN-14 must be 14 characters long.");
        }

        return Optional.empty();
    }

    private Optional<String> validateAlbumName(final String albumName) {

        if (Objects.isNull(albumName)) {
            return Optional.of("Album name is required.");
        } else if (albumName.length() > ALBUM_NAME_MAX_LENGTH) {
            return Optional.of(String.format("Album name must be %d characters or fewer.", ALBUM_NAME_MAX_LENGTH));
        }

        return Optional.empty();
    }

    private Optional<String> validateArtistName(final String artistName) {

        if (Objects.isNull(artistName)) {
            return Optional.of("Artist name is required.");
        } else if (artistName.length() > ARTIST_NAME_MAX_LENGTH) {
            return Optional.of(String.format("Artist name must be %d characters or fewer.", ALBUM_NAME_MAX_LENGTH));
        }

        return Optional.empty();
    }

    private Optional<String> validateSourceAlbum(final String sourceAlbumId) {

        if (Objects.isNull(sourceAlbumId)) {
            return Optional.of("Source album ID is required.");
        }

        return Optional.empty();
    }

    private Optional<String> validateAlbumId(final String albumId) {

        if (Objects.isNull(albumId)) {
            return Optional.of("Album ID is required.");
        }

        return Optional.empty();
    }
}
