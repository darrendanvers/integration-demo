package dev.codestijl.integrationdemo;

import dev.codestijl.integrationdemo.common.IdUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

/**
 * JobParametersIncrementer that will generate an ID to use for the batch ID
 * by the job. It will place the batch ID in the job parameters with a key
 * of 'batchId'.
 *
 * @author darren
 * @since 1.0.0
 */
public class BatchIdParameterGenerator implements JobParametersIncrementer {

    private static final Logger logger = LoggerFactory.getLogger(BatchIdParameterGenerator.class);

    @Override
    public JobParameters getNext(final JobParameters parameters) {

        final String batchId = IdUtils.newId();
        logger.info(String.format("Generated '%s' as batch ID.", batchId));

        return new JobParametersBuilder().addString("batchId", batchId).toJobParameters();
    }
}
