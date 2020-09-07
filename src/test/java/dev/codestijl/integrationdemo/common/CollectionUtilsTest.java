package dev.codestijl.integrationdemo.common;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Tests CollectionUtils.
 *
 * @author darren
 * @since 1.0.0
 */
public class CollectionUtilsTest {

    /**
     * Tests the asString method that takes a max length. The generated string
     * will be smaller than the max length. The method should return the full
     * string.
     */
    @Test
    public void asString_withMaxLength_shorterThanMaxLength_returnsFullString() {

        final List<String> smallList = List.of("value one", "value two");

        final String asString = CollectionUtils.asString(smallList, 1_000);
        Assert.assertEquals("{value one,value two}", asString);
    }

    /**
     * Tests the asString method that takes a max length. The generated string
     * will be larger than the max length. The method should return the abbreviated
     * string still wrapped in curly braces.
     */
    @Test
    public void asString_withMaxLength_longerThanMaxLength_returnsAbbreviatedString() {

        final List<String> longList = List.of("value one", "value two", "value three");

        final String asString = CollectionUtils.asString(longList, 28);
        Assert.assertEquals("{value one,value two,val...}", asString);
    }

    /**
     * Tests the asString method that takes a max length. The list passed in will be empty.
     * The method should return only the curly braces.
     */
    @Test
    public void asString_withMaxLength_emptyList_returnsOnlyCurlyBraces() {

        final String asString = CollectionUtils.asString(List.of());
        Assert.assertEquals("{}", asString);
    }

    /**
     * Tests the asString method that takes a max length. Null will be passed to the function.
     * The method should return only the curly braces.
     */
    @Test
    public void asString_withMaxLength_nullList_returnsOnlyCurlyBraces() {

        final String asString = CollectionUtils.asString(null);
        Assert.assertEquals("{}", asString);
    }
}
