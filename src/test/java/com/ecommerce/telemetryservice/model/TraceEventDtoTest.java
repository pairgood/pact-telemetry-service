package com.ecommerce.telemetryservice.model;

import com.ecommerce.telemetryservice.dto.TraceEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TraceEventDtoTest {

    private TraceEventDto traceEventDto;

    @BeforeEach
    void setUp() {
        traceEventDto = new TraceEventDto();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyDto() {
        // When
        TraceEventDto dto = new TraceEventDto();

        // Then
        assertThat(dto.getTraceId()).isNull();
        assertThat(dto.getSpanId()).isNull();
        assertThat(dto.getParentSpanId()).isNull();
        assertThat(dto.getServiceName()).isNull();
        assertThat(dto.getOperation()).isNull();
        assertThat(dto.getEventType()).isNull();
        assertThat(dto.getTimestamp()).isNull();
        assertThat(dto.getDurationMs()).isNull();
        assertThat(dto.getStatus()).isNull();
        assertThat(dto.getMetadata()).isNull();
        assertThat(dto.getHttpMethod()).isNull();
        assertThat(dto.getHttpUrl()).isNull();
        assertThat(dto.getHttpStatusCode()).isNull();
        assertThat(dto.getErrorMessage()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getCorrelationId()).isNull();
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        traceEventDto.setTraceId("trace-123");
        traceEventDto.setSpanId("span-456");
        traceEventDto.setParentSpanId("parent-span-789");
        traceEventDto.setServiceName("test-service");
        traceEventDto.setOperation("test_operation");
        traceEventDto.setEventType(TraceEvent.EventType.SPAN);
        traceEventDto.setTimestamp(now);
        traceEventDto.setDurationMs(1000L);
        traceEventDto.setStatus(TraceEvent.Status.SUCCESS);
        traceEventDto.setMetadata("{\"key\": \"value\"}");
        traceEventDto.setHttpMethod("POST");
        traceEventDto.setHttpUrl("https://api.example.com/endpoint");
        traceEventDto.setHttpStatusCode(201);
        traceEventDto.setErrorMessage("Error occurred");
        traceEventDto.setUserId("user-123");
        traceEventDto.setCorrelationId("corr-456");

        // Then
        assertThat(traceEventDto.getTraceId()).isEqualTo("trace-123");
        assertThat(traceEventDto.getSpanId()).isEqualTo("span-456");
        assertThat(traceEventDto.getParentSpanId()).isEqualTo("parent-span-789");
        assertThat(traceEventDto.getServiceName()).isEqualTo("test-service");
        assertThat(traceEventDto.getOperation()).isEqualTo("test_operation");
        assertThat(traceEventDto.getEventType()).isEqualTo(TraceEvent.EventType.SPAN);
        assertThat(traceEventDto.getTimestamp()).isEqualTo(now);
        assertThat(traceEventDto.getDurationMs()).isEqualTo(1000L);
        assertThat(traceEventDto.getStatus()).isEqualTo(TraceEvent.Status.SUCCESS);
        assertThat(traceEventDto.getMetadata()).isEqualTo("{\"key\": \"value\"}");
        assertThat(traceEventDto.getHttpMethod()).isEqualTo("POST");
        assertThat(traceEventDto.getHttpUrl()).isEqualTo("https://api.example.com/endpoint");
        assertThat(traceEventDto.getHttpStatusCode()).isEqualTo(201);
        assertThat(traceEventDto.getErrorMessage()).isEqualTo("Error occurred");
        assertThat(traceEventDto.getUserId()).isEqualTo("user-123");
        assertThat(traceEventDto.getCorrelationId()).isEqualTo("corr-456");
    }

    @Test
    void traceEventDto_ShouldAcceptAllEventTypes() {
        // When & Then
        traceEventDto.setEventType(TraceEvent.EventType.SPAN);
        assertThat(traceEventDto.getEventType()).isEqualTo(TraceEvent.EventType.SPAN);

        traceEventDto.setEventType(TraceEvent.EventType.LOG);
        assertThat(traceEventDto.getEventType()).isEqualTo(TraceEvent.EventType.LOG);

        traceEventDto.setEventType(TraceEvent.EventType.METRIC);
        assertThat(traceEventDto.getEventType()).isEqualTo(TraceEvent.EventType.METRIC);
    }

    @Test
    void traceEventDto_ShouldAcceptAllStatusValues() {
        // When & Then
        traceEventDto.setStatus(TraceEvent.Status.SUCCESS);
        assertThat(traceEventDto.getStatus()).isEqualTo(TraceEvent.Status.SUCCESS);

        traceEventDto.setStatus(TraceEvent.Status.ERROR);
        assertThat(traceEventDto.getStatus()).isEqualTo(TraceEvent.Status.ERROR);

        traceEventDto.setStatus(TraceEvent.Status.TIMEOUT);
        assertThat(traceEventDto.getStatus()).isEqualTo(TraceEvent.Status.TIMEOUT);
    }

    @Test
    void traceEventDto_ShouldAcceptNullValues() {
        // When
        traceEventDto.setTraceId(null);
        traceEventDto.setSpanId(null);
        traceEventDto.setParentSpanId(null);
        traceEventDto.setServiceName(null);
        traceEventDto.setOperation(null);
        traceEventDto.setEventType(null);
        traceEventDto.setTimestamp(null);
        traceEventDto.setDurationMs(null);
        traceEventDto.setStatus(null);
        traceEventDto.setMetadata(null);
        traceEventDto.setHttpMethod(null);
        traceEventDto.setHttpUrl(null);
        traceEventDto.setHttpStatusCode(null);
        traceEventDto.setErrorMessage(null);
        traceEventDto.setUserId(null);
        traceEventDto.setCorrelationId(null);

        // Then
        assertThat(traceEventDto.getTraceId()).isNull();
        assertThat(traceEventDto.getSpanId()).isNull();
        assertThat(traceEventDto.getParentSpanId()).isNull();
        assertThat(traceEventDto.getServiceName()).isNull();
        assertThat(traceEventDto.getOperation()).isNull();
        assertThat(traceEventDto.getEventType()).isNull();
        assertThat(traceEventDto.getTimestamp()).isNull();
        assertThat(traceEventDto.getDurationMs()).isNull();
        assertThat(traceEventDto.getStatus()).isNull();
        assertThat(traceEventDto.getMetadata()).isNull();
        assertThat(traceEventDto.getHttpMethod()).isNull();
        assertThat(traceEventDto.getHttpUrl()).isNull();
        assertThat(traceEventDto.getHttpStatusCode()).isNull();
        assertThat(traceEventDto.getErrorMessage()).isNull();
        assertThat(traceEventDto.getUserId()).isNull();
        assertThat(traceEventDto.getCorrelationId()).isNull();
    }

    @Test
    void traceEventDto_ShouldAcceptEmptyStrings() {
        // When
        traceEventDto.setTraceId("");
        traceEventDto.setSpanId("");
        traceEventDto.setParentSpanId("");
        traceEventDto.setServiceName("");
        traceEventDto.setOperation("");
        traceEventDto.setMetadata("");
        traceEventDto.setHttpMethod("");
        traceEventDto.setHttpUrl("");
        traceEventDto.setErrorMessage("");
        traceEventDto.setUserId("");
        traceEventDto.setCorrelationId("");

        // Then
        assertThat(traceEventDto.getTraceId()).isEqualTo("");
        assertThat(traceEventDto.getSpanId()).isEqualTo("");
        assertThat(traceEventDto.getParentSpanId()).isEqualTo("");
        assertThat(traceEventDto.getServiceName()).isEqualTo("");
        assertThat(traceEventDto.getOperation()).isEqualTo("");
        assertThat(traceEventDto.getMetadata()).isEqualTo("");
        assertThat(traceEventDto.getHttpMethod()).isEqualTo("");
        assertThat(traceEventDto.getHttpUrl()).isEqualTo("");
        assertThat(traceEventDto.getErrorMessage()).isEqualTo("");
        assertThat(traceEventDto.getUserId()).isEqualTo("");
        assertThat(traceEventDto.getCorrelationId()).isEqualTo("");
    }

    @Test
    void traceEventDto_ShouldAcceptLongStrings() {
        // Given
        String longString = "a".repeat(1000);

        // When
        traceEventDto.setTraceId(longString);
        traceEventDto.setSpanId(longString);
        traceEventDto.setParentSpanId(longString);
        traceEventDto.setServiceName(longString);
        traceEventDto.setOperation(longString);
        traceEventDto.setMetadata(longString);
        traceEventDto.setHttpMethod(longString);
        traceEventDto.setHttpUrl(longString);
        traceEventDto.setErrorMessage(longString);
        traceEventDto.setUserId(longString);
        traceEventDto.setCorrelationId(longString);

        // Then
        assertThat(traceEventDto.getTraceId()).isEqualTo(longString);
        assertThat(traceEventDto.getSpanId()).isEqualTo(longString);
        assertThat(traceEventDto.getParentSpanId()).isEqualTo(longString);
        assertThat(traceEventDto.getServiceName()).isEqualTo(longString);
        assertThat(traceEventDto.getOperation()).isEqualTo(longString);
        assertThat(traceEventDto.getMetadata()).isEqualTo(longString);
        assertThat(traceEventDto.getHttpMethod()).isEqualTo(longString);
        assertThat(traceEventDto.getHttpUrl()).isEqualTo(longString);
        assertThat(traceEventDto.getErrorMessage()).isEqualTo(longString);
        assertThat(traceEventDto.getUserId()).isEqualTo(longString);
        assertThat(traceEventDto.getCorrelationId()).isEqualTo(longString);
    }

    @Test
    void traceEventDto_ShouldAcceptSpecialCharacters() {
        // Given
        String specialChars = "!@#$%^&*()_+{}|:<>?-=[]\\;'\",./ 中文 🚀";

        // When
        traceEventDto.setTraceId(specialChars);
        traceEventDto.setSpanId(specialChars);
        traceEventDto.setServiceName(specialChars);
        traceEventDto.setOperation(specialChars);
        traceEventDto.setMetadata(specialChars);
        traceEventDto.setErrorMessage(specialChars);

        // Then
        assertThat(traceEventDto.getTraceId()).isEqualTo(specialChars);
        assertThat(traceEventDto.getSpanId()).isEqualTo(specialChars);
        assertThat(traceEventDto.getServiceName()).isEqualTo(specialChars);
        assertThat(traceEventDto.getOperation()).isEqualTo(specialChars);
        assertThat(traceEventDto.getMetadata()).isEqualTo(specialChars);
        assertThat(traceEventDto.getErrorMessage()).isEqualTo(specialChars);
    }

    @Test
    void traceEventDto_ShouldAcceptNegativeDuration() {
        // When
        traceEventDto.setDurationMs(-100L);

        // Then
        assertThat(traceEventDto.getDurationMs()).isEqualTo(-100L);
    }

    @Test
    void traceEventDto_ShouldAcceptZeroDuration() {
        // When
        traceEventDto.setDurationMs(0L);

        // Then
        assertThat(traceEventDto.getDurationMs()).isEqualTo(0L);
    }

    @Test
    void traceEventDto_ShouldAcceptLargeDuration() {
        // When
        traceEventDto.setDurationMs(Long.MAX_VALUE);

        // Then
        assertThat(traceEventDto.getDurationMs()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void traceEventDto_ShouldAcceptNegativeHttpStatusCode() {
        // When
        traceEventDto.setHttpStatusCode(-1);

        // Then
        assertThat(traceEventDto.getHttpStatusCode()).isEqualTo(-1);
    }

    @Test
    void traceEventDto_ShouldAcceptLargeHttpStatusCode() {
        // When
        traceEventDto.setHttpStatusCode(999);

        // Then
        assertThat(traceEventDto.getHttpStatusCode()).isEqualTo(999);
    }

    @Test
    void traceEventDto_ShouldAcceptFutureTimestamp() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusYears(10);

        // When
        traceEventDto.setTimestamp(futureDate);

        // Then
        assertThat(traceEventDto.getTimestamp()).isEqualTo(futureDate);
    }

    @Test
    void traceEventDto_ShouldAcceptPastTimestamp() {
        // Given
        LocalDateTime pastDate = LocalDateTime.now().minusYears(10);

        // When
        traceEventDto.setTimestamp(pastDate);

        // Then
        assertThat(traceEventDto.getTimestamp()).isEqualTo(pastDate);
    }
}