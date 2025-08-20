package com.ecommerce.telemetryservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceEventTest {

    private Validator validator;
    private TraceEvent traceEvent;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        traceEvent = new TraceEvent();
        traceEvent.setTraceId("trace-123");
        traceEvent.setSpanId("span-456");
        traceEvent.setServiceName("test-service");
        traceEvent.setOperation("test_operation");
        traceEvent.setTimestamp(LocalDateTime.now());
        traceEvent.setStatus(TraceEvent.Status.SUCCESS);
        traceEvent.setEventType(TraceEvent.EventType.SPAN);
    }

    @Test
    void validTraceEvent_ShouldPassValidation() {
        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void traceEventWithNullTraceId_ShouldFailValidation() {
        // Given
        traceEvent.setTraceId(null);

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("traceId");
    }

    @Test
    void traceEventWithNullSpanId_ShouldFailValidation() {
        // Given
        traceEvent.setSpanId(null);

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("spanId");
    }

    @Test
    void traceEventWithNullServiceName_ShouldFailValidation() {
        // Given
        traceEvent.setServiceName(null);

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("serviceName");
    }

    @Test
    void traceEventWithNullOperation_ShouldFailValidation() {
        // Given
        traceEvent.setOperation(null);

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("operation");
    }

    @Test
    void traceEventWithNullTimestamp_ShouldFailValidation() {
        // Given
        traceEvent.setTimestamp(null);

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("timestamp");
    }

    @Test
    void defaultConstructor_ShouldSetDefaultValues() {
        // When
        TraceEvent event = new TraceEvent();

        // Then
        assertThat(event.getTimestamp()).isNotNull();
        assertThat(event.getStatus()).isEqualTo(TraceEvent.Status.SUCCESS);
        assertThat(event.getEventType()).isEqualTo(TraceEvent.EventType.SPAN);
    }

    @Test
    void parameterizedConstructor_ShouldSetRequiredFields() {
        // When
        TraceEvent event = new TraceEvent("trace-456", "span-789", "my-service", "my_operation");

        // Then
        assertThat(event.getTraceId()).isEqualTo("trace-456");
        assertThat(event.getSpanId()).isEqualTo("span-789");
        assertThat(event.getServiceName()).isEqualTo("my-service");
        assertThat(event.getOperation()).isEqualTo("my_operation");
        assertThat(event.getTimestamp()).isNotNull();
        assertThat(event.getStatus()).isEqualTo(TraceEvent.Status.SUCCESS);
        assertThat(event.getEventType()).isEqualTo(TraceEvent.EventType.SPAN);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        traceEvent.setId(100L);
        traceEvent.setParentSpanId("parent-span-123");
        traceEvent.setDurationMs(500L);
        traceEvent.setMetadata("{\"key\": \"value\"}");
        traceEvent.setHttpMethod("POST");
        traceEvent.setHttpUrl("http://example.com/api");
        traceEvent.setHttpStatusCode(201);
        traceEvent.setErrorMessage("Something went wrong");
        traceEvent.setUserId("user-123");
        traceEvent.setCorrelationId("corr-456");

        // Then
        assertThat(traceEvent.getId()).isEqualTo(100L);
        assertThat(traceEvent.getParentSpanId()).isEqualTo("parent-span-123");
        assertThat(traceEvent.getDurationMs()).isEqualTo(500L);
        assertThat(traceEvent.getMetadata()).isEqualTo("{\"key\": \"value\"}");
        assertThat(traceEvent.getHttpMethod()).isEqualTo("POST");
        assertThat(traceEvent.getHttpUrl()).isEqualTo("http://example.com/api");
        assertThat(traceEvent.getHttpStatusCode()).isEqualTo(201);
        assertThat(traceEvent.getErrorMessage()).isEqualTo("Something went wrong");
        assertThat(traceEvent.getUserId()).isEqualTo("user-123");
        assertThat(traceEvent.getCorrelationId()).isEqualTo("corr-456");
    }

    @Test
    void eventTypeEnum_ShouldHaveCorrectValues() {
        // Then
        assertThat(TraceEvent.EventType.values()).containsExactly(
            TraceEvent.EventType.SPAN, 
            TraceEvent.EventType.LOG, 
            TraceEvent.EventType.METRIC
        );
    }

    @Test
    void statusEnum_ShouldHaveCorrectValues() {
        // Then
        assertThat(TraceEvent.Status.values()).containsExactly(
            TraceEvent.Status.SUCCESS, 
            TraceEvent.Status.ERROR, 
            TraceEvent.Status.TIMEOUT
        );
    }

    @Test
    void eventTypeEnum_ShouldBeStoredAsString() {
        // Given
        traceEvent.setEventType(TraceEvent.EventType.LOG);

        // When & Then
        assertThat(traceEvent.getEventType()).isEqualTo(TraceEvent.EventType.LOG);
        assertThat(traceEvent.getEventType().name()).isEqualTo("LOG");
    }

    @Test
    void statusEnum_ShouldBeStoredAsString() {
        // Given
        traceEvent.setStatus(TraceEvent.Status.ERROR);

        // When & Then
        assertThat(traceEvent.getStatus()).isEqualTo(TraceEvent.Status.ERROR);
        assertThat(traceEvent.getStatus().name()).isEqualTo("ERROR");
    }

    @Test
    void traceEventWithAllOptionalFields_ShouldPassValidation() {
        // Given
        traceEvent.setParentSpanId("parent-span");
        traceEvent.setDurationMs(1000L);
        traceEvent.setMetadata("{\"test\": \"data\"}");
        traceEvent.setHttpMethod("GET");
        traceEvent.setHttpUrl("https://api.example.com/endpoint");
        traceEvent.setHttpStatusCode(200);
        traceEvent.setErrorMessage(null); // Optional field
        traceEvent.setUserId("user-456");
        traceEvent.setCorrelationId("correlation-789");

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void traceEventWithMinimalRequiredFields_ShouldPassValidation() {
        // Given - Only required fields set, optional fields are null
        TraceEvent minimalEvent = new TraceEvent();
        minimalEvent.setTraceId("trace-minimal");
        minimalEvent.setSpanId("span-minimal");
        minimalEvent.setServiceName("minimal-service");
        minimalEvent.setOperation("minimal_operation");
        minimalEvent.setTimestamp(LocalDateTime.now());

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(minimalEvent);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void traceEventConstructor_ShouldSetTimestampToCurrentTime() {
        // Given
        LocalDateTime before = LocalDateTime.now();

        // When
        TraceEvent event = new TraceEvent();
        
        // Then
        LocalDateTime after = LocalDateTime.now();
        assertThat(event.getTimestamp()).isAfterOrEqualTo(before);
        assertThat(event.getTimestamp()).isBeforeOrEqualTo(after);
    }

    @Test
    void traceEventWithLongMetadata_ShouldPassValidation() {
        // Given
        StringBuilder longMetadata = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longMetadata.append("This is a very long metadata string. ");
        }
        traceEvent.setMetadata(longMetadata.toString());

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void traceEventFields_ShouldAcceptNullForOptionalFields() {
        // Given
        traceEvent.setParentSpanId(null);
        traceEvent.setDurationMs(null);
        traceEvent.setMetadata(null);
        traceEvent.setHttpMethod(null);
        traceEvent.setHttpUrl(null);
        traceEvent.setHttpStatusCode(null);
        traceEvent.setErrorMessage(null);
        traceEvent.setUserId(null);
        traceEvent.setCorrelationId(null);

        // When
        Set<ConstraintViolation<TraceEvent>> violations = validator.validate(traceEvent);

        // Then
        assertThat(violations).isEmpty();
    }
}