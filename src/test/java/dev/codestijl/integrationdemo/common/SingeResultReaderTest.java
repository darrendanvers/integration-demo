package dev.codestijl.integrationdemo.common;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Tests SingleResultReader.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootTest
public class SingeResultReaderTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<SrrTestClass> ROW_MAPPER = (rs, rowNum) ->
            new SrrTestClass()
                    .setId(rs.getLong("ID"))
                    .setValue(rs.getString("VALUE"));

    private static final SingleResultReader<SrrTestClass> SINGLE_RESULT_READER =
            new SingleResultReader<>(ROW_MAPPER);

    /**
     * A class to store the results of querying the SRR_TEST table.
     *
     * @author darren
     * @since 1.0.0
     */
    private static final class SrrTestClass {

        private Long id;
        private String value;

        /**
         * Returns the ID.
         *
         * @return The ID.
         */
        public Long getId() {
            return id;
        }

        /**
         * Sets the ID.
         *
         * @param id The ID to save.
         * @return This object for further configuration.
         */
        public SrrTestClass setId(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * Returns the value.
         *
         * @return The value.
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value.
         *
         * @param value The value to save.
         * @return This object for further configuration.
         */
        public SrrTestClass setValue(final String value) {
            this.value = value;
            return this;
        }
    }

    /**
     * Runs a query that will return no rows back. The object returned should be null.
     */
    @Test
    public void extractData_noResults_returnsNull() {

        final SrrTestClass result = this.jdbcTemplate.query("SELECT ID, VALUE FROM SRR.SRR_TEST WHERE ID = 3",
                SINGLE_RESULT_READER);
        Assert.assertNull(result);
    }

    /**
     * Runs a query that will return 1 row back. The object returned should be populated with the
     * data from that row.
     */
    @Test
    public void extractData_oneResult_returnsObject() {

        final SrrTestClass result = this.jdbcTemplate.query("SELECT ID, VALUE FROM SRR.SRR_TEST WHERE ID = 1",
                SINGLE_RESULT_READER);
        Assert.assertNotNull(result);
        Assert.assertEquals(Long.valueOf(1L), result.getId());
        Assert.assertEquals("ROW ONE", result.getValue());
    }

    /**
     * Runs a query that will return 2 rows back. This should throw an error.
     */
    @Test
    public void extractData_twoResults_throwsError() {

        Assert.assertThrows(IncorrectResultSizeDataAccessException.class,
            () -> this.jdbcTemplate.query("SELECT ID, VALUE FROM SRR.SRR_TEST", SINGLE_RESULT_READER));
    }
}
