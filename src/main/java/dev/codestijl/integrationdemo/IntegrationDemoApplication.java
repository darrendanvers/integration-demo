package dev.codestijl.integrationdemo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Driver application for the integration demo.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootApplication
@ImportResource("classpath:/jobs.xml")
@EnableBatchProcessing
// This class needs to be set up with a public constructor and cannot be final in order for Spring to load
// the context correctly.
@SuppressWarnings("PMD.UseUtilityClass")
public class IntegrationDemoApplication {

    /**
     * The main method. It will delegate all processing to Spring.
     *
     * @param args The application's command line arguments.
     */
    public static void main(final String[] args) {
        SpringApplication.run(IntegrationDemoApplication.class, args);
    }
}
