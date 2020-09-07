package dev.codestijl.integrationdemo;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.stereotype.Component;

/**
 * Configures the batch environment. Since this is just a
 * sample application, I'm not persisting the Spring
 * batch metadata anywhere. This class will configure
 * the environment to use an in-memory map to maintain
 * job information.
 *
 * @author darren
 * @since 1.0.0
 */
@Component
public class BatchConfig extends DefaultBatchConfigurer {

    @Override
    public void setDataSource(final DataSource dataSource) {

        // Intentionally empty. Not persisting the DataSource
        // will trigger Spring to use the in-memory storage
        // of job information.
    }
}
