package dev.codestijl.integrationdemo;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

/**
 * Tests BatchIdParameterGenerator.
 *
 * @author darren
 * @since 1.0.0
 */
public class BatchIdParameterGeneratorTest {

    /**
     * Calls getNext and ensures it adds a batchId parameter to the JobParameters and that
     * batchId is a GUID.
     */
    @Test
    public void getNext_returnsParametersWithBatchId() {

        final BatchIdParameterGenerator batchIdParameterGenerator = new BatchIdParameterGenerator();

        final JobParameters oldParameters = new JobParametersBuilder().toJobParameters();

        final JobParameters jobParameters = batchIdParameterGenerator.getNext(oldParameters);

        Assert.assertTrue(jobParameters.getParameters().containsKey("batchId"));
        Assert.assertTrue(TestUtils.isGuid(jobParameters.getString("batchId")));
    }
}
