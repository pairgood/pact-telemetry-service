package com.ecommerce.telemetryservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI telemetryServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8086");
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("support@ecommerce.com");
        contact.setName("E-Commerce Support");
        contact.setUrl("https://www.ecommerce.com");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Telemetry Service API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for distributed tracing and observability in the e-commerce microservices ecosystem. " +
                            "Collect telemetry data, traces, metrics from all services. Provide trace analysis, timeline visualization, and system monitoring.")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}