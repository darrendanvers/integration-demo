package dev.codestijl.integrationdemo.loadbatch;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

/**
 * Tasklet that takes a raw file and loads it into the BATCH table.
 *
 * @author darren
 * @since 1.0.0
 */
public class BatchTasklet implements Tasklet {

    private static final Logger logger = LoggerFactory.getLogger(BatchTasklet.class);

    private static final String INSERT_SQL = "INSERT INTO STAGE.BATCH " +
            "(BATCH_ID, CREATE_TIME, PAYLOAD) " +
            "VALUES (?, ?, ?)";

    private final String filePath;
    private final JdbcTemplate jdbcTemplate;
    private final String batchId;

    /**
     * Creates a new BatchTasklet.
     *
     * @param dataSource The DataSource to use to run queries.
     * @param filePath The path to the file to load into the BATCH table.
     * @param batchId The ID to use for this batch.
     */
    public BatchTasklet(final DataSource dataSource, final String filePath, final String batchId) {

        Assert.notNull(dataSource, "Datasource cannot be null.");
        Assert.notNull(filePath, "File path cannot be null.");
        Assert.notNull(batchId, "Batch ID cannot be null.");

        this.filePath = filePath;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.batchId = batchId;
    }

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) throws Exception {

        logger.info(String.format("Loading staging with batch '%s'.", this.batchId));

        // Add the ID to the job parameters so that it can be used by later steps.
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("batchId", this.batchId);

        // Stream the file into a CLOB field in the BATCH table.
        try (InputStreamReader inputStreamReader = new
                InputStreamReader(this.getClass().getResourceAsStream(this.filePath), StandardCharsets.UTF_8)) {

            this.jdbcTemplate.update(INSERT_SQL, (ps) -> {
                ps.setString(1, this.batchId);
                ps.setTimestamp(2, Timestamp.from(Instant.now()));
                ps.setClob(3, inputStreamReader);
            });
        }

        // It only wrote 1 record.
        contribution.incrementWriteCount(1);

        return RepeatStatus.FINISHED;
    }
}
