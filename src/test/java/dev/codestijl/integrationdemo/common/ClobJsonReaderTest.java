package dev.codestijl.integrationdemo.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests ClobJsonReader.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootTest
// These test propagate the signature from calling read().
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class ClobJsonReaderTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Class to hold the object stored as JSON in the CJR_TEST table.
     *
     * @author darren
     * @since 1.0.0
     */
    public static final class JsonTestClass {

        private int id;
        private final List<Integer> values = new LinkedList<>();

        /**
         * Returns the ID.
         *
         * @return The ID.
         */
        public int getId() {
            return id;
        }

        /**
         * Sets the ID.
         *
         * @param id The ID to save.
         * @return This object for further configuration.
         */
        public JsonTestClass setId(final int id) {
            this.id = id;
            return this;
        }

        /**
         * Returns the list of values.
         *
         * @return The list of values.
         */
        public List<Integer> getValues() {
            return values;
        }

        /**
         * Sets the list of values.
         *
         * @param values The list of values to save.
         * @return This object for further configuration.
         */
        public JsonTestClass setValues(final List<Integer> values) {
            this.values.clear();
            this.values.addAll(values);
            return this;
        }
    }

    /**
     * Reads all the records from the CJR_TEST table. It will ensure that each call to read returns
     * one JsonTestClass and that all JsonTestClasses are read from the DB.
     *
     * @throws Exception Any error thrown by ClobJsonReader.
     */
    @Test
    public void read_multipleRows_multipleValues_returnsAllValues() throws Exception {

        final ClobJsonReader<JsonTestClass> clobJsonReader = new ClobJsonReader<>(this.dataSource,
                "SELECT VALUE FROM CJR_TEST",
                JsonTestClass.class);

        // The StepExecution is not used.
        clobJsonReader.beforeStep(Mockito.mock(StepExecution.class));

        int totalReturned = 0;
        for (JsonTestClass testData = clobJsonReader.read();
             Objects.nonNull(testData);
             testData = clobJsonReader.read()) {

            Assert.assertEquals(totalReturned, testData.id);
            Assert.assertEquals(totalReturned, testData.values.size());
            totalReturned++;
        }

        // The StepExecution is not used.
        clobJsonReader.afterStep(Mockito.mock(StepExecution.class));

        Assert.assertEquals(4, totalReturned);
    }

    /**
     * Reads a single row from the CJR_TEST table. This method uses the constructor that sets the
     * PreparedStatementSetter. It will ensure all JsonTestClass objects from that row are returned and no
     * JsonTestClass objects from other rows are returned.
     *
     * @throws Exception Any error thrown by ClobJsonReader.
     */
    @Test
    public void read_singleRow_withPreparedStatementSetter_multipleValues_returnsAllValues() throws Exception {

        final ClobJsonReader<JsonTestClass> clobJsonReader = new ClobJsonReader<>(this.dataSource,
                "SELECT VALUE FROM CJR_TEST WHERE ID = ?",
                JsonTestClass.class, (ps) -> ps.setLong(1, 1L));

        // The StepExecution is not used.
        clobJsonReader.beforeStep(Mockito.mock(StepExecution.class));

        int totalReturned = 0;
        for (JsonTestClass testData = clobJsonReader.read();
             Objects.nonNull(testData);
             testData = clobJsonReader.read()) {

            Assert.assertEquals(totalReturned, testData.id);
            Assert.assertEquals(totalReturned, testData.values.size());
            totalReturned++;
        }

        // The StepExecution is not used.
        clobJsonReader.afterStep(Mockito.mock(StepExecution.class));

        Assert.assertEquals(2, totalReturned);
    }

    /**
     * Tests that calling read without calling beforeStep does not throw an error, but also does
     * not return any data.
     *
     * @throws Exception Any error thrown by ClobJsonReader.
     */
    @Test
    public void read_beforeStepNotCalled_returnsNull() throws Exception {

        final ClobJsonReader<JsonTestClass> clobJsonReader = new ClobJsonReader<>(this.dataSource,
                "SELECT VALUE FROM CJR_TEST WHERE ID = ?",
                JsonTestClass.class, (ps) -> ps.setLong(1, 1L));

        Assert.assertNull(clobJsonReader.read());
    }
}
