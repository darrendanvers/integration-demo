package dev.codestijl.integrationdemo.common;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

/**
 * Class that can be used as an ItemReader for tables that have a CLOB that is storing JSON data. It will
 * pull data from the CLOB, parse the JSON, and return the results one object at a time. The cursor can hold
 * multiple records of objects, but, if a row contains a JSON array, each element of the array will be retuned
 * individually.
 *
 * @param <T> The type of object being stored in the JSON array.
 */
public class ClobJsonReader<T> implements ItemReader<T>, StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(ClobJsonReader.class);

    private final String sql;
    private final DataSource dataSource;
    private final PreparedStatementSetter preparedStatementSetter;
    private final Class<? extends T> clazz;

    private int rowsRead;
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Clob clob;
    private MappingIterator<T> mappingIterator;

    private boolean hasResult;

    /**
     * Error thrown when this class can't be configured.
     *
     * @author darren
     * @since 1.0.0
     */
    public static final class ClobReaderConfigurationException extends RuntimeException {

        private static final long serialVersionUID = 4322234228110505755L;

        /**
         * Constructs a new ClobReaderConfigurationException.
         *
         * @param message The error message.
         */
        public ClobReaderConfigurationException(final   String message) {
            super(message);
        }
    }

    /**
     * Constructs a new ClobJsonReader. This overloading should be used when the SQL contains no bind variables.
     *
     * @param dataSource The DataSource to use to run queries.
     * @param sql The SQL to run to obtain a cursor with JSON data. See the notes on the other constructor for
     *            requirements for the SQL.
     * @param clazz The concrete class instance of the type this reader will return.
     */
    public ClobJsonReader(final DataSource dataSource, final String sql, final Class<? extends T> clazz) {

        this(dataSource, sql, clazz, null);
    }

    /**
     * Constructs a new ClobJsonReader. This overloading should be used when the SQL contains bind variables.
     *
     * @param dataSource The DataSource to use to run queries.
     * @param sql The SQL to run to obtain a cursor with JSON data. This query should return only one column: the column
     *            tha contains the CLOB.  This query can contain any number of bind variables. These should be set
     *            by the PreparedStatementSetter passed in the preparedStatementSetter parameter. This query can return
     *            any number of rows.
     * @param clazz The concrete class instance of the type this reader will return.
     * @param preparedStatementSetter The PreparedStatementSetter that sets the bind variables when executing the query.
     */
    public ClobJsonReader(final DataSource dataSource, final String sql, final Class<? extends T> clazz,
                          final PreparedStatementSetter preparedStatementSetter) {

        Assert.notNull(dataSource, "DataSource cannot be null.");
        Assert.notNull(sql, "SQL cannot be null");
        Assert.notNull(clazz, "Concrete class cannot be null");
        // preparedStatementSetter can by null.

        this.dataSource = dataSource;
        this.sql = sql;
        this.clazz = clazz;
        this.preparedStatementSetter = preparedStatementSetter;
    }

    @Override
    public T read() throws Exception {

        // If the last advance of the cursor returned nothing, we're done.
        if (!this.hasResult) {
            return null;
        }

        // If there's data left in the iterator, return it.
        if (this.mappingIterator.hasNext()) {
            this.rowsRead++;
            return this.mappingIterator.next();
        }

        // If not, read the next record and call this function again as
        // all the data will be reset.
        this.advanceToNextRecord();
        return this.read();
    }

    @Override
    public void beforeStep(final StepExecution stepExecution) {

        this.rowsRead = 0;

        try {

            // Connect to the DB.
            this.connection = DataSourceUtils.getConnection(this.dataSource);

            // Prepare to run the query.
            this.preparedStatement = this.connection.prepareStatement(this.sql);

            // If we've got a preparedStatement, use it to set the bind variables.
            if (Objects.nonNull(this.preparedStatement)) {
                this.preparedStatementSetter.setValues(this.preparedStatement);
            }

            // Run the query.
            this.resultSet = this.preparedStatement.executeQuery();

            // Set the data up for a read.
            this.advanceToNextRecord();
        } catch (SQLException e) {

            e.forEach(error -> logger.error(e.getLocalizedMessage()));
            throw (ClobReaderConfigurationException) new ClobReaderConfigurationException("Unable to extract data from CLOB.").initCause(e);
        } catch (IOException e) {

            logger.error(e.getLocalizedMessage());
            throw (ClobReaderConfigurationException) new ClobReaderConfigurationException("Unable to extract data from CLOB.").initCause(e);
        }
    }

    private void advanceToNextRecord() throws SQLException, IOException {

        logger.debug("Reading next record.");

        // Release the CLOB.
        this.freeClob();

        // Read the next row from the cursor.
        this.hasResult = this.resultSet.next();

        if (this.hasResult) {

            logger.debug("Next record available.");

            // Set up the data to return.
            this.clob = resultSet.getClob(1);

            final ObjectMapper objectMapper = new ObjectMapper();
            this.mappingIterator = objectMapper.readerFor(this.clazz).readValues(clob.getCharacterStream());
        } else {

            logger.debug("At end of results.");
            // Otherwise, make an empty iterator.
            this.mappingIterator = MappingIterator.emptyIterator();
        }
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {

        logger.info(String.format("%,d rows read.", this.rowsRead));

        // Try and close everything. At this point, if we get errors, just log them.
        this.freeClob();
        JdbcUtils.closeResultSet(this.resultSet);
        JdbcUtils.closeStatement(this.preparedStatement);
        JdbcUtils.closeConnection(this.connection);

        return ExitStatus.COMPLETED;
    }

    private void freeClob() {

        try {
            if (Objects.nonNull(this.clob)) {
                this.clob.free();
            }
        } catch (SQLException e) {

            e.forEach(error -> logger.error(e.getLocalizedMessage()));
        }
    }
}
