package dev.codestijl.integrationdemo.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.util.Assert;

/**
 * Base class for BatchPreparedStatementSetters.
 *
 * @param <T> The type of object that this class will insert into the DB.
 */
/* default */ abstract class BaseBatchPreparedStatementSetter<T> implements BatchPreparedStatementSetter {

    private final List<T> data;

    /**
     * Constructs a new BaseBatchPreparedStatementSetter.
     *
     * @param data The Collection of objects that will be inserted, updated, or deleted by extending classes.
     */
    protected BaseBatchPreparedStatementSetter(final Collection<? extends T> data) {

        Assert.notNull(data, "Data cannot be null.");
        this.data = new ArrayList<>(data);
    }

    /**
     * Extending classes should override this method to do the actual processing of an object.
     *
     * @param preparedStatement The PreparedStatement to set the values in.
     * @param value The Object to use to set values in the PreparedStatement.
     * @throws SQLException Any error when interacting with the DB.
     */
    protected abstract void doSetValues(PreparedStatement preparedStatement, T value) throws SQLException;

    @Override
    public void setValues(final PreparedStatement preparedStatement, final int rowNumber) throws SQLException {

        final T value = this.data.get(rowNumber);
        this.doSetValues(preparedStatement, value);
    }

    @Override
    public int getBatchSize() {

        return this.data.size();
    }
}
