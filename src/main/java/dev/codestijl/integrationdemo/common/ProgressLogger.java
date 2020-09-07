package dev.codestijl.integrationdemo.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to keep track of the number of things processed and then periodically
 * log how much has been processed.
 *
 * @author darren
 * @since 1.0.0
 */
// By design, I'm allowing any class that uses this one to override the logger so that
// the log messages come from that logger.
@SuppressWarnings("PMD.MoreThanOneLogger")
public final class ProgressLogger {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(ProgressLogger.class);
    private static final int DEFAULT_LOG_AT = 5_000;

    private final Logger logger;
    private final int logAt;

    private int count;

    /**
     * Builder for the ProcessLogger.
     *
     * @author darren
     * @since 1.0.0
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static final class Builder {
        private Logger logger;
        private int logAt;

        private Builder() {
            this.logAt = DEFAULT_LOG_AT;
            this.logger = DEFAULT_LOGGER;
        }

        /**
         * Returns a new ProgressLogger.
         *
         * @return A new ProgressLogger.
         */
        public ProgressLogger build() {
            return new ProgressLogger(this.logger, this.logAt);
        }
    }

    private ProgressLogger(final Logger logger, final int logAt) {
        this.logger = logger;
        this.logAt = logAt;
    }

    /**
     * Returns a Builder to use to construct a new ProgressLogger.
     *
     * @return A Builder to use to construct a new ProgressLogger.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Resets the number of things processed.
     */
    public void reset() {
        this.count = 0;
    }

    /**
     * Increases the number of things processed by 1.
     */
    public void incrementCount() {
        this.incrementCount(1);
    }

    /**
     * Increases the number of things processed by a specified amount.
     *
     * @param toIncrement How much to increment the count by.
     */
    public void incrementCount(final int toIncrement) {

        this.count += toIncrement;

        if (this.count % this.logAt == 0) {
            this.log();
        }
    }

    /**
     * Writes the number of things processed to the log.
     */
    public void log() {
        logger.info(String.format("%,d rows processed.", this.count));
    }
}
