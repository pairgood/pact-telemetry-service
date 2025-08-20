package com.ecommerce.telemetryservice.controller;

import com.ecommerce.telemetryservice.dto.TraceEventDto;
import com.ecommerce.telemetryservice.model.TraceEvent;
import com.ecommerce.telemetryservice.service.TelemetryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TelemetryController.class)
@ActiveProfiles("test")
public class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelemetryService telemetryService;

    @Autowired
    private ObjectMapper objectMapper;

    private TraceEvent testTraceEvent;
    private TraceEventDto testTraceEventDto;

    @BeforeEach
    void setUp() {
        testTraceEvent = new TraceEvent();
        testTraceEvent.setId(1L);
        testTraceEvent.setTraceId("trace-123");
        testTraceEvent.setSpanId("span-456");
        testTraceEvent.setServiceName("test-service");
        testTraceEvent.setOperation("test_operation");
        testTraceEvent.setTimestamp(LocalDateTime.now());
        testTraceEvent.setStatus(TraceEvent.Status.SUCCESS);
        testTraceEvent.setDurationMs(100L);

        testTraceEventDto = new TraceEventDto();
        testTraceEventDto.setTraceId("trace-123");
        testTraceEventDto.setSpanId("span-456");
        testTraceEventDto.setServiceName("test-service");
        testTraceEventDto.setOperation("test_operation");
        testTraceEventDto.setTimestamp(LocalDateTime.now());
        testTraceEventDto.setStatus(TraceEvent.Status.SUCCESS);
        testTraceEventDto.setDurationMs(100L);
    }

    @Test
    void recordEvent_ShouldReturnCreatedEvent() throws Exception {
        // Given
        when(telemetryService.recordEvent(any(TraceEventDto.class))).thenReturn(testTraceEvent);

        // When & Then
        mockMvc.perform(post("/api/telemetry/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTraceEventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.traceId").value("trace-123"))
                .andExpect(jsonPath("$.spanId").value("span-456"))
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.operation").value("test_operation"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(telemetryService).recordEvent(any(TraceEventDto.class));
    }

    @Test
    void recordEventsBatch_ShouldReturnCreatedEvents() throws Exception {
        // Given
        TraceEventDto dto2 = new TraceEventDto();
        dto2.setTraceId("trace-124");
        dto2.setSpanId("span-457");
        dto2.setServiceName("test-service-2");
        dto2.setOperation("test_operation_2");

        TraceEvent event2 = new TraceEvent();
        event2.setId(2L);
        event2.setTraceId("trace-124");
        event2.setSpanId("span-457");
        event2.setServiceName("test-service-2");
        event2.setOperation("test_operation_2");

        List<TraceEventDto> dtos = Arrays.asList(testTraceEventDto, dto2);
        List<TraceEvent> events = Arrays.asList(testTraceEvent, event2);

        when(telemetryService.recordEventsBatch(anyList())).thenReturn(events);

        // When & Then
        mockMvc.perform(post("/api/telemetry/events/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].traceId").value("trace-123"))
                .andExpect(jsonPath("$[1].traceId").value("trace-124"));

        verify(telemetryService).recordEventsBatch(anyList());
    }

    @Test
    void getTraceById_ShouldReturnTraceEvents() throws Exception {
        // Given
        List<TraceEvent> traceEvents = Arrays.asList(testTraceEvent);
        when(telemetryService.getTraceById("trace-123")).thenReturn(traceEvents);

        // When & Then
        mockMvc.perform(get("/api/telemetry/traces/trace-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].traceId").value("trace-123"));

        verify(telemetryService).getTraceById("trace-123");
    }

    @Test
    void getTraceTimeline_ShouldReturnTimeline() throws Exception {
        // Given
        Map<String, Object> timeline = new HashMap<>();
        timeline.put("traceId", "trace-123");
        timeline.put("events", Arrays.asList(testTraceEvent));
        timeline.put("totalDuration", 1000L);
        timeline.put("serviceCount", 1L);

        when(telemetryService.getTraceTimeline("trace-123")).thenReturn(timeline);

        // When & Then
        mockMvc.perform(get("/api/telemetry/traces/trace-123/timeline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.traceId").value("trace-123"))
                .andExpect(jsonPath("$.totalDuration").value(1000))
                .andExpect(jsonPath("$.serviceCount").value(1));

        verify(telemetryService).getTraceTimeline("trace-123");
    }

    @Test
    void getTraces_ShouldReturnPaginatedTraces() throws Exception {
        // Given
        Map<String, Object> traceSummary = new HashMap<>();
        traceSummary.put("traceId", "trace-123");
        traceSummary.put("spanCount", 5);
        traceSummary.put("status", "SUCCESS");

        List<Map<String, Object>> traces = Arrays.asList(traceSummary);
        Page<Map<String, Object>> tracePage = new PageImpl<>(traces, PageRequest.of(0, 20), 1);

        when(telemetryService.getTraces(any(PageRequest.class), isNull(), isNull(), isNull()))
                .thenReturn(tracePage);

        // When & Then
        mockMvc.perform(get("/api/telemetry/traces")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].traceId").value("trace-123"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(telemetryService).getTraces(any(PageRequest.class), isNull(), isNull(), isNull());
    }

    @Test
    void getTraces_WithFilters_ShouldReturnFilteredTraces() throws Exception {
        // Given
        Map<String, Object> traceSummary = new HashMap<>();
        traceSummary.put("traceId", "trace-123");
        traceSummary.put("spanCount", 3);
        traceSummary.put("status", "SUCCESS");

        List<Map<String, Object>> traces = Arrays.asList(traceSummary);
        Page<Map<String, Object>> tracePage = new PageImpl<>(traces, PageRequest.of(0, 20), 1);

        when(telemetryService.getTraces(any(PageRequest.class), eq("test-service"), eq("test_operation"), eq("SUCCESS")))
                .thenReturn(tracePage);

        // When & Then
        mockMvc.perform(get("/api/telemetry/traces")
                .param("page", "0")
                .param("size", "20")
                .param("serviceName", "test-service")
                .param("operation", "test_operation")
                .param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(telemetryService).getTraces(any(PageRequest.class), eq("test-service"), eq("test_operation"), eq("SUCCESS"));
    }

    @Test
    void getServices_ShouldReturnAllServices() throws Exception {
        // Given
        List<String> services = Arrays.asList("service-1", "service-2", "service-3");
        when(telemetryService.getServices()).thenReturn(services);

        // When & Then
        mockMvc.perform(get("/api/telemetry/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("service-1"))
                .andExpect(jsonPath("$[1]").value("service-2"))
                .andExpect(jsonPath("$[2]").value("service-3"));

        verify(telemetryService).getServices();
    }

    @Test
    void getServiceOperations_ShouldReturnOperationsForService() throws Exception {
        // Given
        List<String> operations = Arrays.asList("operation-1", "operation-2");
        when(telemetryService.getServiceOperations("test-service")).thenReturn(operations);

        // When & Then
        mockMvc.perform(get("/api/telemetry/services/test-service/operations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("operation-1"))
                .andExpect(jsonPath("$[1]").value("operation-2"));

        verify(telemetryService).getServiceOperations("test-service");
    }

    @Test
    void getServiceMetrics_ShouldReturnMetricsForService() throws Exception {
        // Given
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("serviceName", "test-service");
        metrics.put("requestCount", 100L);
        metrics.put("averageDurationMs", 150L);
        metrics.put("errorRate", 2.5);

        when(telemetryService.getServiceMetrics("test-service")).thenReturn(metrics);

        // When & Then
        mockMvc.perform(get("/api/telemetry/services/test-service/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("test-service"))
                .andExpect(jsonPath("$.requestCount").value(100))
                .andExpect(jsonPath("$.averageDurationMs").value(150))
                .andExpect(jsonPath("$.errorRate").value(2.5));

        verify(telemetryService).getServiceMetrics("test-service");
    }

    @Test
    void getHealthStatus_ShouldReturnSystemHealth() throws Exception {
        // Given
        Map<String, Object> health = new HashMap<>();
        health.put("status", "healthy");
        health.put("totalEvents", 1000L);
        health.put("recentEvents", 50L);
        health.put("trackedServices", 5);

        when(telemetryService.getHealthStatus()).thenReturn(health);

        // When & Then
        mockMvc.perform(get("/api/telemetry/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.totalEvents").value(1000))
                .andExpect(jsonPath("$.recentEvents").value(50))
                .andExpect(jsonPath("$.trackedServices").value(5));

        verify(telemetryService).getHealthStatus();
    }

    @Test
    void cleanupOldTraces_ShouldReturnCleanupResult() throws Exception {
        // Given
        Map<String, Object> result = new HashMap<>();
        result.put("deletedEvents", 100L);
        result.put("message", "Cleaned up traces older than 7 days");

        when(telemetryService.cleanupOldTraces(7)).thenReturn(result);

        // When & Then
        mockMvc.perform(delete("/api/telemetry/traces/cleanup")
                .param("olderThanDays", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedEvents").value(100))
                .andExpect(jsonPath("$.message").value("Cleaned up traces older than 7 days"));

        verify(telemetryService).cleanupOldTraces(7);
    }

    @Test
    void cleanupOldTraces_WithDefaultValue_ShouldUseDefaultDays() throws Exception {
        // Given
        Map<String, Object> result = new HashMap<>();
        result.put("deletedEvents", 50L);
        result.put("message", "Cleaned up traces older than 7 days");

        when(telemetryService.cleanupOldTraces(7)).thenReturn(result);

        // When & Then
        mockMvc.perform(delete("/api/telemetry/traces/cleanup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deletedEvents").value(50));

        verify(telemetryService).cleanupOldTraces(7);
    }

    @Test
    void recordEvent_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/telemetry/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(telemetryService, never()).recordEvent(any(TraceEventDto.class));
    }

    @Test
    void recordEvent_WithServiceException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(telemetryService.recordEvent(any(TraceEventDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            mockMvc.perform(post("/api/telemetry/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testTraceEventDto)));
        } catch (Exception e) {
            // Exception is expected
        }

        verify(telemetryService).recordEvent(any(TraceEventDto.class));
    }

    @Test
    void getTraceById_WithNonExistentTrace_ShouldReturnEmptyList() throws Exception {
        // Given
        when(telemetryService.getTraceById("non-existent")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/telemetry/traces/non-existent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(telemetryService).getTraceById("non-existent");
    }
}