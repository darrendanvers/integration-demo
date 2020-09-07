package dev.codestijl.integrationdemo;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests that the Spring test environment loads.
 *
 * @author darren
 * @since 1.0.0
 */
@SpringBootTest
class IntegrationDemoApplicationTests {

    /**
     * Tests that the Spring context loads.
     */
    @Test
    public void contextLoads() {
        Assert.assertTrue(true);
    }

}
