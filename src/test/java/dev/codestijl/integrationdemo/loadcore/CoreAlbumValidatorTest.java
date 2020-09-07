package dev.codestijl.integrationdemo.loadcore;

import dev.codestijl.integrationdemo.common.CollectionUtils;
import dev.codestijl.integrationdemo.common.ValidationException;
import dev.codestijl.integrationdemo.entity.CoreAlbum;

import java.util.Objects;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Tests CoreAlbumValidator.
 *
 * @author darren
 * @since 1.0.0
 */
public class CoreAlbumValidatorTest {

    private final CoreAlbumValidator validator = new CoreAlbumValidator();

    /**
     * Calls validate with a null gtin14 and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_nullGtin_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 1")
                .setAlbumId("1235")
                .setArtistName("artist 1")
                .setSourceAlbumId("33657");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "GTIN-14 name is required.")));
    }

    /**
     * Calls validate with a short gtin14 and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_shortGtin_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 2")
                .setAlbumId("56889")
                .setArtistName("artist 2")
                .setGtin14("12345")
                .setSourceAlbumId("56987");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "GTIN-14 must be 14 characters long.")));
    }

    /**
     * Calls validate with a long gtin14 and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_longGtin_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 3")
                .setAlbumId("369874")
                .setArtistName("artist 3")
                .setGtin14("123456789012345")
                .setSourceAlbumId("125478");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "GTIN-14 must be 14 characters long.")));
    }

    /**
     * Calls validate with a null albumName and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_noAlbumName_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumId("78965442")
                .setArtistName("artist 4")
                .setGtin14("12345678901234")
                .setSourceAlbumId("366547");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "Album name is required.")));
    }

    /**
     * Calls validate with a long albumName and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_longAlbumName_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("dafdjoiejfkew0dafdkjadjklflkvdkjakjdfdaekljdkalijelkelksckjiciodkelskidoielksjnbviuoiesliewbgjkljseiu")
                .setAlbumId("789512")
                .setArtistName("artist 5")
                .setGtin14("12345678901234")
                .setSourceAlbumId("896145");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "Album name must be 100 characters or fewer.")));
    }

    /**
     * Calls validate with a null artistName and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_noArtistName_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 6")
                .setAlbumId("561348")
                .setGtin14("43210987654321")
                .setSourceAlbumId("1457");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "Artist name is required.")));
    }

    /**
     * Calls validate with a long artistName and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_longArtistName_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 7")
                .setAlbumId("457878")
                .setArtistName("dfklanmveiowhhgacm;,vnaioewnfklamdlkasdhiohwvdm,a;kjdkjwklen.dsjklsjnds.,dmelkls,kdlsl.ke,d.slke.klss")
                .setGtin14("78945862310456")
                .setSourceAlbumId("36987");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "Artist name must be 100 characters or fewer.")));
    }

    /**
     * Calls validate with a null sourceAlbumId and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_noSourceAlbumId_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 8")
                .setAlbumId("12388")
                .setArtistName("artist 8")
                .setGtin14("98756213504598");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "Source album ID is required.")));
    }

    /**
     * Calls validate with a null albumId and ensures the validation fails
     * with the correct error message.
     */
    @Test
    public void validate_noAlbumId_throwsException() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("album 9")
                .setGtin14("89546512302589")
                .setArtistName("artist 9")
                .setSourceAlbumId("13323");

        final ValidationException exception = Assert.assertThrows(ValidationException.class, () -> this.validator.validate(coreAlbum));
        Assert.assertTrue(exception.getErrors().stream().anyMatch(m -> Objects.equals(m, "Album ID is required.")));
    }

    /**
     * Calls validate with a valid object that hits the validator's boundary contitions
     * and ensures it does not fail.
     */
    @Test
    public void validate_boundaryConditions_passes() {

        final CoreAlbum coreAlbum = new CoreAlbum()
                .setAlbumName("dsajkdkl;fjakljdfkajklvmndkljakldjsfkdjlskdlksjlkds.,dkljlskdklskljdfkvkjdlskwoinvskdjkjkdlkjvmlksje")
                .setGtin14("56987453212569")
                .setArtistName("a;dkjfak;djsfkajkl;mnvakljewnvkldjskladklsjfga;kdnvklamnkl;dfjakdjfkajsd;kfj;kldjflksdjflksdjlkjfljd")
                .setAlbumId("13323")
                .setSourceAlbumId("24554");

        try {
            this.validator.validate(coreAlbum);
        } catch (ValidationException e) {
            Assert.fail(String.format("Failed validation: %s.", CollectionUtils.asString(e.getErrors())));
        }
    }
}
