package com.ecommerce.telemetryservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void telemetryServiceOpenAPI_ShouldReturnValidOpenAPIConfiguration() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveCorrectInfo() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();
        Info info = openAPI.getInfo();

        // Then
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Telemetry Service API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).contains("distributed tracing and observability");
        assertThat(info.getDescription()).contains("e-commerce microservices ecosystem");
        assertThat(info.getDescription()).contains("telemetry data, traces, metrics");
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveCorrectContact() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Then
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("E-Commerce Support");
        assertThat(contact.getEmail()).isEqualTo("support@ecommerce.com");
        assertThat(contact.getUrl()).isEqualTo("https://www.ecommerce.com");
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveCorrectLicense() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Then
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://choosealicense.com/licenses/mit/");
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveCorrectServers() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();

        // Then
        assertThat(openAPI.getServers()).isNotNull();
        assertThat(openAPI.getServers()).hasSize(1);
        
        Server server = openAPI.getServers().get(0);
        assertThat(server.getUrl()).isEqualTo("http://localhost:8086");
        assertThat(server.getDescription()).isEqualTo("Server URL in Development environment");
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveConsistentConfiguration() {
        // When
        OpenAPI openAPI1 = openApiConfig.telemetryServiceOpenAPI();
        OpenAPI openAPI2 = openApiConfig.telemetryServiceOpenAPI();

        // Then
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
        assertThat(openAPI1.getInfo().getVersion()).isEqualTo(openAPI2.getInfo().getVersion());
        assertThat(openAPI1.getServers().get(0).getUrl()).isEqualTo(openAPI2.getServers().get(0).getUrl());
    }

    @Test
    void openApiConfig_ShouldBeInstantiable() {
        // When
        OpenApiConfig config = new OpenApiConfig();

        // Then
        assertThat(config).isNotNull();
    }

    @Test
    void telemetryServiceOpenAPI_InfoShouldNotBeNull() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();

        // Then
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isNotNull();
        assertThat(openAPI.getInfo().getVersion()).isNotNull();
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getInfo().getDescription()).isNotNull();
    }

    @Test
    void telemetryServiceOpenAPI_ServersShouldNotBeEmpty() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();

        // Then
        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers().get(0).getUrl()).isNotEmpty();
        assertThat(openAPI.getServers().get(0).getDescription()).isNotEmpty();
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveValidContactDetails() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Then
        assertThat(contact.getEmail()).contains("@");
        assertThat(contact.getUrl()).startsWith("https://");
        assertThat(contact.getName()).isNotEmpty();
    }

    @Test
    void telemetryServiceOpenAPI_ShouldHaveValidLicenseUrl() {
        // When
        OpenAPI openAPI = openApiConfig.telemetryServiceOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Then
        assertThat(license.getUrl()).startsWith("https://");
        assertThat(license.getName()).contains("MIT");
    }
}