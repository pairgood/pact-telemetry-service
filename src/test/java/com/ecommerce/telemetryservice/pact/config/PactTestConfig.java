package com.ecommerce.telemetryservice.pact.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Test configuration for Pact consumer tests.
 * This configuration will be used when external service dependencies are added.
 */
@TestConfiguration
@TestPropertySource(properties = {
    "logging.level.au.com.dius.pact=DEBUG"
})
public class PactTestConfig {
    
    // Future external service client configurations will go here
    // Example:
    // @Bean
    // @Primary
    // public ExternalServiceClient mockableClient() {
    //     return new ExternalServiceClient(); // Should accept base URL
    // }
}