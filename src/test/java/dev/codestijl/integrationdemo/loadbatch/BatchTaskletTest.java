package dev.codestijl.integrationdemo.loadbatch;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Tests BatchTasklet.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootTest
// execute's signature is throws Exception.
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class BatchTaskletTest {

    @Autowired
    private DataSource dataSource;

    /**
     * Calls execute in the BatchTasklet and checks that it sets the write count, returns the right value, and
     * validates that the file passed to the class is loaded into the CLOB.
     */
    @Test
    public void execute_loadsData() {

        final BatchTasklet batchTasklet = new BatchTasklet(this.dataSource, "/clob-load-test.txt", "5558880003a");

        final StepExecution mockStepExecution = Mockito.mock(StepExecution.class);
        final StepContribution stepContribution = new StepContribution(mockStepExecution);
        final ChunkContext chunkContext = Mockito.mock(ChunkContext.class);

        try {
            final RepeatStatus returnedStatus = batchTasklet.execute(stepContribution, chunkContext);
            Assert.assertEquals(1, stepContribution.getWriteCount());
            Assert.assertEquals(RepeatStatus.FINISHED, returnedStatus);
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        }

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);

        final String savedValue = jdbcTemplate.queryForObject("SELECT PAYLOAD FROM STAGE.BATCH WHERE BATCH_ID = '5558880003a'", String.class);
        Assert.assertEquals("TEST FILE.", savedValue);
    }

    /**
     * Calls execute and loads a big file just to make sure it will load.
     */
    @Test
    public void execute_loadsLargeFile() {

        final BatchTasklet batchTasklet = new BatchTasklet(this.dataSource, "/large-file.json", "5558880003b");

        final StepExecution mockStepExecution = Mockito.mock(StepExecution.class);
        final StepContribution stepContribution = new StepContribution(mockStepExecution);
        final ChunkContext chunkContext = Mockito.mock(ChunkContext.class);

        try {
            final RepeatStatus returnedStatus = batchTasklet.execute(stepContribution, chunkContext);
            Assert.assertEquals(1, stepContribution.getWriteCount());
            Assert.assertEquals(RepeatStatus.FINISHED, returnedStatus);
        } catch (Exception e) {
            Assert.fail(e.getLocalizedMessage());
        }
    }
}
